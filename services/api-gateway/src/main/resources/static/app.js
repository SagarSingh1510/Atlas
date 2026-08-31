const state = {
  token: localStorage.getItem("atlas.token") || "",
  user: null,
  authMode: "login",
  selectedWorkspaceId: null,
  selectedDiagramId: null,
  deployments: [],
};

const els = {
  authState: document.querySelector("#authState"),
  loginTab: document.querySelector("#loginTab"),
  registerTab: document.querySelector("#registerTab"),
  emailField: document.querySelector("#emailField"),
  authForm: document.querySelector("#authForm"),
  authSubmit: document.querySelector("#authSubmit"),
  email: document.querySelector("#email"),
  username: document.querySelector("#username"),
  password: document.querySelector("#password"),
  currentUser: document.querySelector("#currentUser"),
  logoutButton: document.querySelector("#logoutButton"),
  refreshHealth: document.querySelector("#refreshHealth"),
  serviceHealth: document.querySelector("#serviceHealth"),
  workspaceForm: document.querySelector("#workspaceForm"),
  workspaceName: document.querySelector("#workspaceName"),
  workspaceList: document.querySelector("#workspaceList"),
  taskForm: document.querySelector("#taskForm"),
  taskTitle: document.querySelector("#taskTitle"),
  taskDescription: document.querySelector("#taskDescription"),
  taskList: document.querySelector("#taskList"),
  diagramForm: document.querySelector("#diagramForm"),
  diagramName: document.querySelector("#diagramName"),
  diagramDefinition: document.querySelector("#diagramDefinition"),
  diagramList: document.querySelector("#diagramList"),
  refreshWorkflow: document.querySelector("#refreshWorkflow"),
  deploymentList: document.querySelector("#deploymentList"),
  simulationList: document.querySelector("#simulationList"),
  reviewList: document.querySelector("#reviewList"),
  toast: document.querySelector("#toast"),
};

const services = [
  ["Gateway", "/api/v1/gateway/health"],
  ["Auth", "/api/v1/auth/health"],
  ["Workspace", "/api/v1/workspaces"],
  ["Diagram", "/api/v1/diagrams/health"],
  ["Deployment", "/api/v1/deployments/health"],
  ["Simulation", "/api/v1/simulations/health"],
  ["AI Review", "/api/v1/ai-reviews/health"],
];

function headers() {
  const base = { "Content-Type": "application/json" };
  if (state.token) {
    base.Authorization = `Bearer ${state.token}`;
  }
  return base;
}

async function api(path, options = {}) {
  const response = await fetch(path, {
    ...options,
    headers: {
      ...headers(),
      ...(options.headers || {}),
    },
  });

  if (response.status === 204) {
    return null;
  }

  const text = await response.text();
  let payload = null;
  if (text) {
    try {
      payload = JSON.parse(text);
    } catch {
      payload = { message: text };
    }
  }

  if (!response.ok) {
    const validationMessage = payload?.errors ? Object.values(payload.errors).join(" ") : "";
    const message = validationMessage || payload?.message || `Request failed with status ${response.status}`;
    throw new Error(message);
  }

  return payload;
}

function showToast(message) {
  els.toast.textContent = message;
  els.toast.classList.remove("hidden");
  window.clearTimeout(showToast.timer);
  showToast.timer = window.setTimeout(() => els.toast.classList.add("hidden"), 3200);
}

function setAuthMode(mode) {
  state.authMode = mode;
  els.loginTab.classList.toggle("active", mode === "login");
  els.registerTab.classList.toggle("active", mode === "register");
  els.emailField.classList.toggle("hidden", mode !== "register");
  els.authSubmit.textContent = mode === "login" ? "Login" : "Create Account";
}

function renderAuth() {
  const signedIn = Boolean(state.token);
  els.authState.textContent = signedIn ? "Signed in" : "Signed out";
  els.authState.classList.toggle("muted", !signedIn);
  els.currentUser.textContent = state.user ? `${state.user.username} (${state.user.email})` : "No active user";
}

function statusClass(status) {
  if (!status) return "muted";
  if (["FAILED", "DOWN"].includes(status)) return "danger";
  if (["PENDING"].includes(status)) return "warn";
  return "";
}

function empty(message) {
  return `<div class="empty">${message}</div>`;
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

async function loadCurrentUser() {
  if (!state.token) {
    renderAuth();
    return;
  }
  try {
    state.user = await api("/api/v1/users/me");
  } catch (error) {
    localStorage.removeItem("atlas.token");
    state.token = "";
    state.user = null;
    showToast("Session expired. Login again.");
  }
  renderAuth();
}

async function loadHealth() {
  const checks = await Promise.all(services.map(async ([name, path]) => {
    try {
      if (name === "Workspace") {
        await api(path);
        return { name, status: state.token ? "UP" : "AUTH" };
      }
      const result = await api(path);
      return { name, status: result?.status || "UP" };
    } catch (error) {
      return { name, status: name === "Workspace" && !state.token ? "AUTH" : "DOWN" };
    }
  }));

  els.serviceHealth.innerHTML = checks.map(check => `
    <div class="item">
      <div class="item-title">
        <span>${check.name}</span>
        <span class="status-pill ${statusClass(check.status)}">${check.status}</span>
      </div>
    </div>
  `).join("");
}

async function loadWorkspaces() {
  if (!state.token) {
    els.workspaceList.innerHTML = empty("Login to load workspaces.");
    return;
  }
  const workspaces = await api("/api/v1/workspaces");
  if (!state.selectedWorkspaceId && workspaces.length) {
    state.selectedWorkspaceId = workspaces[0].id;
  }
  els.workspaceList.innerHTML = workspaces.length ? workspaces.map(workspace => `
    <article class="item ${workspace.id === state.selectedWorkspaceId ? "active" : ""}">
      <div class="item-title">
        <span>${escapeHtml(workspace.name)}</span>
        <span class="status-pill">#${workspace.id}</span>
      </div>
      <p class="item-meta">Owner ${workspace.ownerId}</p>
      <div class="item-actions">
        <button class="small-button" type="button" data-select-workspace="${workspace.id}">Open</button>
        <button class="small-button" type="button" data-delete-workspace="${workspace.id}">Delete</button>
      </div>
    </article>
  `).join("") : empty("No workspaces yet.");
}

async function loadTasks() {
  if (!state.token || !state.selectedWorkspaceId) {
    els.taskList.innerHTML = empty("Select a workspace to view tasks.");
    return;
  }
  const tasks = await api(`/api/v1/workspaces/${state.selectedWorkspaceId}/tasks`);
  els.taskList.innerHTML = tasks.length ? tasks.map(task => `
    <article class="item">
      <div class="item-title">
        <span>${escapeHtml(task.title)}</span>
        <span class="status-pill ${statusClass(task.status)}">${escapeHtml(task.status)}</span>
      </div>
      <p class="item-meta">${escapeHtml(task.description || "No description")}</p>
    </article>
  `).join("") : empty("No tasks in this workspace.");
}

async function loadDiagrams() {
  if (!state.token || !state.selectedWorkspaceId) {
    els.diagramList.innerHTML = empty("Select a workspace to view diagrams.");
    return;
  }
  const diagrams = await api(`/api/v1/workspaces/${state.selectedWorkspaceId}/diagrams`);
  if (!state.selectedDiagramId && diagrams.length) {
    state.selectedDiagramId = diagrams[0].id;
  }
  els.diagramList.innerHTML = diagrams.length ? diagrams.map(diagram => `
    <article class="item ${diagram.id === state.selectedDiagramId ? "active" : ""}">
      <div class="item-title">
        <span>${escapeHtml(diagram.name)}</span>
        <span class="status-pill">#${diagram.id}</span>
      </div>
      <p class="item-meta">${escapeHtml(diagram.definition)}</p>
      <div class="item-actions">
        <button class="small-button" type="button" data-select-diagram="${diagram.id}">Open</button>
        <button class="small-button" type="button" data-deploy-diagram="${diagram.id}">Deploy</button>
        <button class="small-button" type="button" data-delete-diagram="${diagram.id}">Delete</button>
      </div>
    </article>
  `).join("") : empty("No diagrams in this workspace.");
}

async function loadWorkflow() {
  els.deploymentList.innerHTML = state.selectedDiagramId ? empty("Loading deployments...") : empty("Select a diagram.");
  els.simulationList.innerHTML = empty("Select a deployment.");
  els.reviewList.innerHTML = empty("Select a deployment.");

  if (!state.token || !state.selectedDiagramId) return;

  state.deployments = await api(`/api/v1/diagrams/${state.selectedDiagramId}/deployments`);
  els.deploymentList.innerHTML = state.deployments.length ? state.deployments.map(deployment => `
    <article class="item">
      <div class="item-title">
        <span>Deployment #${deployment.id}</span>
        <span class="status-pill ${statusClass(deployment.status)}">${deployment.status}</span>
      </div>
      <p class="item-meta">Simulation ${deployment.simulationId || "not created"} · Review ${deployment.aiReviewId || "not created"}</p>
      <div class="item-actions">
        <button class="small-button" type="button" data-load-deployment="${deployment.id}">Inspect</button>
      </div>
    </article>
  `).join("") : empty("No deployments for this diagram.");
}

async function loadDeploymentDetails(deploymentId) {
  const [simulations, reviews] = await Promise.all([
    api(`/api/v1/deployments/${deploymentId}/simulations`),
    api(`/api/v1/deployments/${deploymentId}/ai-reviews`),
  ]);

  els.simulationList.innerHTML = simulations.length ? simulations.map(simulation => `
    <article class="item">
      <div class="item-title">
        <span>Simulation #${simulation.id}</span>
        <span class="status-pill ${statusClass(simulation.status)}">${simulation.status}</span>
      </div>
      <p class="item-meta">${escapeHtml(simulation.summary)}</p>
    </article>
  `).join("") : empty("No simulations recorded.");

  els.reviewList.innerHTML = reviews.length ? reviews.map(review => `
    <article class="item">
      <div class="item-title">
        <span>Review #${review.id}</span>
        <span class="status-pill">${review.score}/100</span>
      </div>
      <p class="item-meta">${escapeHtml(review.summary)}</p>
    </article>
  `).join("") : empty("No AI reviews recorded.");
}

async function refreshWorkspaceViews() {
  await loadWorkspaces();
  await Promise.all([loadTasks(), loadDiagrams()]);
  await loadWorkflow();
}

els.loginTab.addEventListener("click", () => setAuthMode("login"));
els.registerTab.addEventListener("click", () => setAuthMode("register"));
els.refreshHealth.addEventListener("click", loadHealth);
els.refreshWorkflow.addEventListener("click", loadWorkflow);

els.authForm.addEventListener("submit", async event => {
  event.preventDefault();
  try {
    if (state.authMode === "register") {
      if (els.password.value.length < 8 || els.password.value.length > 20) {
        showToast("Password must be 8 to 20 characters.");
        return;
      }
      await api("/api/v1/auth/register", {
        method: "POST",
        body: JSON.stringify({
          username: els.username.value,
          email: els.email.value,
          password: els.password.value,
        }),
      });
      setAuthMode("login");
      showToast("Account created. Login with the same credentials.");
      return;
    }

    const response = await api("/api/v1/auth/login", {
      method: "POST",
      body: JSON.stringify({ username: els.username.value, password: els.password.value }),
    });
    state.token = response.token;
    localStorage.setItem("atlas.token", state.token);
    await loadCurrentUser();
    await refreshWorkspaceViews();
    await loadHealth();
    showToast("Logged in.");
  } catch (error) {
    showToast(error.message);
  }
});

els.logoutButton.addEventListener("click", async () => {
  localStorage.removeItem("atlas.token");
  state.token = "";
  state.user = null;
  state.selectedWorkspaceId = null;
  state.selectedDiagramId = null;
  renderAuth();
  await refreshWorkspaceViews();
  await loadHealth();
});

els.workspaceForm.addEventListener("submit", async event => {
  event.preventDefault();
  try {
    const workspace = await api("/api/v1/workspaces", {
      method: "POST",
      body: JSON.stringify({ name: els.workspaceName.value }),
    });
    els.workspaceName.value = "";
    state.selectedWorkspaceId = workspace.id;
    state.selectedDiagramId = null;
    await refreshWorkspaceViews();
  } catch (error) {
    showToast(error.message);
  }
});

els.taskForm.addEventListener("submit", async event => {
  event.preventDefault();
  if (!state.selectedWorkspaceId) {
    showToast("Select a workspace first.");
    return;
  }
  try {
    await api(`/api/v1/workspaces/${state.selectedWorkspaceId}/tasks`, {
      method: "POST",
      body: JSON.stringify({ title: els.taskTitle.value, description: els.taskDescription.value }),
    });
    els.taskTitle.value = "";
    els.taskDescription.value = "";
    await loadTasks();
  } catch (error) {
    showToast(error.message);
  }
});

els.diagramForm.addEventListener("submit", async event => {
  event.preventDefault();
  if (!state.selectedWorkspaceId) {
    showToast("Select a workspace first.");
    return;
  }
  try {
    const diagram = await api(`/api/v1/workspaces/${state.selectedWorkspaceId}/diagrams`, {
      method: "POST",
      body: JSON.stringify({ name: els.diagramName.value, definition: els.diagramDefinition.value }),
    });
    els.diagramName.value = "";
    els.diagramDefinition.value = "";
    state.selectedDiagramId = diagram.id;
    await loadDiagrams();
    await loadWorkflow();
  } catch (error) {
    showToast(error.message);
  }
});

document.addEventListener("click", async event => {
  const target = event.target;
  if (!(target instanceof HTMLElement)) return;

  const workspaceId = target.dataset.selectWorkspace;
  const deleteWorkspaceId = target.dataset.deleteWorkspace;
  const diagramId = target.dataset.selectDiagram;
  const deployDiagramId = target.dataset.deployDiagram;
  const deleteDiagramId = target.dataset.deleteDiagram;
  const deploymentId = target.dataset.loadDeployment;

  try {
    if (workspaceId) {
      state.selectedWorkspaceId = Number(workspaceId);
      state.selectedDiagramId = null;
      await refreshWorkspaceViews();
    }
    if (deleteWorkspaceId) {
      await api(`/api/v1/workspaces/${deleteWorkspaceId}`, { method: "DELETE" });
      if (state.selectedWorkspaceId === Number(deleteWorkspaceId)) {
        state.selectedWorkspaceId = null;
        state.selectedDiagramId = null;
      }
      await refreshWorkspaceViews();
    }
    if (diagramId) {
      state.selectedDiagramId = Number(diagramId);
      await loadDiagrams();
      await loadWorkflow();
    }
    if (deployDiagramId) {
      const deployment = await api(`/api/v1/diagrams/${deployDiagramId}/deploy`, { method: "POST" });
      showToast(`Deployment ${deployment.status.toLowerCase()}.`);
      state.selectedDiagramId = Number(deployDiagramId);
      await loadWorkflow();
      if (deployment.id) {
        await loadDeploymentDetails(deployment.id);
      }
    }
    if (deleteDiagramId) {
      await api(`/api/v1/diagrams/${deleteDiagramId}`, { method: "DELETE" });
      if (state.selectedDiagramId === Number(deleteDiagramId)) {
        state.selectedDiagramId = null;
      }
      await loadDiagrams();
      await loadWorkflow();
    }
    if (deploymentId) {
      await loadDeploymentDetails(Number(deploymentId));
    }
  } catch (error) {
    showToast(error.message);
  }
});

async function init() {
  setAuthMode("login");
  renderAuth();
  await loadCurrentUser();
  await loadHealth();
  await refreshWorkspaceViews();
}

init().catch(error => showToast(error.message));
