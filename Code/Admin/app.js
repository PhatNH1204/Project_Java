const tokenKey = "smart-heritage-admin-token";
const loginScreen = document.querySelector("#login-screen");
const appShell = document.querySelector("#app-shell");
const viewContainer = document.querySelector("#view-container");
const messageBox = document.querySelector("#app-message");
const titles = {
  dashboard: "Tổng quan",
  sites: "Khu di tích",
  artifacts: "Hiện vật",
  beacons: "iBeacon",
  feedback: "Đánh giá",
  users: "Tài khoản",
  languages: "Ngôn ngữ",
};

let currentView = "dashboard";
let cachedSites = [];
let cachedArtifacts = [];
let cachedLanguages = [];

function escapeHtml(value) {
  return String(value ?? "").replace(/[&<>"']/g, (character) => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;",
  })[character]);
}

function authHeader() {
  return { Authorization: `Bearer ${sessionStorage.getItem(tokenKey) ?? ""}` };
}

async function request(path, options = {}) {
  const headers = { ...authHeader(), ...(options.headers ?? {}) };
  if (options.body && !(options.body instanceof FormData)) {
    headers["Content-Type"] = "application/json";
  }
  const response = await fetch(path, { ...options, headers });
  if (!response.ok) {
    const body = await response.json().catch(() => null);
    throw new Error(body?.message ?? `Yêu cầu thất bại (${response.status})`);
  }
  if (response.status === 204) return null;
  const type = response.headers.get("content-type") ?? "";
  return type.includes("application/json") ? response.json() : response;
}

function showMessage(message = "", isError = false) {
  messageBox.textContent = message;
  messageBox.classList.toggle("error", isError);
}

function showApp(username) {
  loginScreen.classList.add("hidden");
  appShell.classList.remove("hidden");
  document.querySelector("#current-user").textContent = username;
  loadView(currentView);
}

async function loadView(view) {
  currentView = view;
  document.querySelector("#page-title").textContent = titles[view];
  document.querySelectorAll(".nav-link").forEach((link) => {
    link.classList.toggle("active", link.dataset.view === view);
  });
  showMessage();
  viewContainer.innerHTML = '<div class="empty">Đang tải dữ liệu...</div>';
  try {
    const renderers = {
      dashboard: renderDashboard,
      sites: renderSites,
      artifacts: renderArtifacts,
      beacons: renderBeacons,
      feedback: renderFeedback,
      users: renderUsers,
      languages: renderLanguages,
    };
    await renderers[view]();
  } catch (error) {
    viewContainer.innerHTML = "";
    showMessage(error.message, true);
  }
}

function table(headers, rows, emptyText = "Chưa có dữ liệu") {
  if (!rows.length) return `<div class="empty">${escapeHtml(emptyText)}</div>`;
  return `<div class="table-wrap"><table><thead><tr>${headers.map((value) => `<th>${escapeHtml(value)}</th>`).join("")}</tr></thead><tbody>${rows.join("")}</tbody></table></div>`;
}

async function renderDashboard() {
  const period = document.querySelector("#report-period").value;
  const result = await request(`/api/analytics/dashboard?period=${encodeURIComponent(period)}`);
  const data = result.data;
  const cards = [
    ["Tổng khách tham quan", data.totalVisitors],
    ["Khách trong kỳ", data.visitorsInPeriod],
    ["Lượt tương tác", data.interactionsInPeriod],
    ["Thời lượng TB (giây)", data.averageEngagementSeconds],
  ];
  const artifactRows = data.popularArtifacts.map((item) =>
    `<tr><td>${escapeHtml(item.artifactCode)}</td><td>${escapeHtml(item.interactions)}</td></tr>`);
  const languageRows = Object.entries(data.languageDistribution).map(([code, count]) =>
    `<tr><td>${escapeHtml(code)}</td><td>${escapeHtml(count)}</td></tr>`);
  viewContainer.innerHTML = `
    <div class="stats-grid">${cards.map(([label, value]) => `<article class="stat-card"><span>${escapeHtml(label)}</span><strong>${escapeHtml(value)}</strong></article>`).join("")}</div>
    <div class="dashboard-grid">
      <section class="panel"><h2>Hiện vật được quan tâm</h2>${table(["Mã hiện vật", "Lượt"], artifactRows)}</section>
      <section class="panel"><h2>Ngôn ngữ tương tác</h2>${table(["Ngôn ngữ", "Lượt"], languageRows)}
        <p class="muted">Đánh giá trung bình: ${escapeHtml(data.averageRating)} / 5 · Chờ duyệt: ${escapeHtml(data.pendingFeedback)}</p>
      </section>
    </div>`;
}

async function renderSites() {
  const result = await request("/api/admin/sites");
  cachedSites = result.data;
  const rows = cachedSites.map((site) => `<tr>
    <td>${escapeHtml(site.name)}</td><td>${escapeHtml(site.address)}</td>
    <td><span class="badge">${site.active ? "Đang hoạt động" : "Đã ẩn"}</span></td>
    <td><button class="button danger" data-action="deactivate-site" data-id="${site.id}">Ẩn</button></td></tr>`);
  viewContainer.innerHTML = `
    <form id="site-form" class="form-card">
      <div class="section-heading"><h2>Thêm khu di tích</h2></div>
      <div class="form-grid">
        <label>Tên khu di tích<input name="name" maxlength="160" required></label>
        <label>Địa chỉ<input name="address" maxlength="255"></label>
        <label class="wide">Mô tả<textarea name="description" maxlength="10000"></textarea></label>
        <label>Vĩ độ<input name="latitude" type="number" step="any" min="-90" max="90"></label>
        <label>Kinh độ<input name="longitude" type="number" step="any" min="-180" max="180"></label>
      </div><div class="form-actions"><button class="button primary">Tạo khu di tích</button></div>
    </form>
    ${table(["Tên", "Địa chỉ", "Trạng thái", "Thao tác"], rows)}`;
}

async function renderArtifacts() {
  const [artifactResult, siteResult, languageResult] = await Promise.all([
    request("/api/admin/artifacts?lang=vi"),
    request("/api/admin/sites"),
    request("/api/admin/languages"),
  ]);
  cachedArtifacts = artifactResult.data;
  cachedSites = siteResult.data;
  cachedLanguages = languageResult.data;
  const siteOptions = cachedSites.filter((site) => site.active)
    .map((site) => `<option value="${site.id}">${escapeHtml(site.name)}</option>`).join("");
  const languageOptions = cachedLanguages.filter((language) => language.active)
    .map((language) => `<option value="${escapeHtml(language.code)}">${escapeHtml(language.name)} (${escapeHtml(language.code)})</option>`).join("");
  const rows = cachedArtifacts.map((artifact) => `<tr>
    <td>${escapeHtml(artifact.code)}</td><td>${escapeHtml(artifact.content?.title ?? "Chưa có nội dung")}</td>
    <td>${escapeHtml(artifact.siteName)}</td><td>${escapeHtml(artifact.locationName)}</td>
    <td><span class="badge">${artifact.active ? "Đang hoạt động" : "Đã ẩn"}</span></td>
    <td><div class="row-actions">
      <button class="button outline" data-action="edit-translation" data-id="${artifact.id}">Bản dịch</button>
      <button class="button danger" data-action="deactivate-artifact" data-id="${artifact.id}">Ẩn</button>
    </div></td></tr>`);
  viewContainer.innerHTML = `
    <form id="artifact-form" class="form-card">
      <div class="section-heading"><h2>Thêm hiện vật và nội dung</h2></div>
      <div class="form-grid">
        <label>Khu di tích<select name="siteId" required>${siteOptions}</select></label>
        <label>Mã hiện vật<input name="code" maxlength="50" required></label>
        <label>Tên vị trí<input name="locationName" maxlength="160"></label>
        <label>Ngôn ngữ<select name="languageCode" required>${languageOptions}</select></label>
        <label>Tiêu đề<input name="title" maxlength="200" required></label>
        <label class="wide">Mô tả<textarea name="description" maxlength="10000" required></textarea></label>
        <label>Ảnh<input name="imageUrl" placeholder="https://..."></label>
        <label>Video<input name="videoUrl" placeholder="https://..."></label>
        <label>Âm thanh<input name="audioUrl" placeholder="https://..."></label>
        <label>Tải ảnh<input name="imageFile" type="file" accept="image/jpeg,image/png,image/webp"></label>
        <label>Tải video<input name="videoFile" type="file" accept="video/mp4,video/webm"></label>
        <label>Tải âm thanh<input name="audioFile" type="file" accept="audio/mpeg,audio/ogg,audio/wav"></label>
      </div><div class="form-actions"><button class="button primary">Tạo hiện vật</button></div>
    </form>
    ${table(["Mã", "Tiêu đề (vi)", "Khu di tích", "Vị trí", "Trạng thái", "Thao tác"], rows)}`;
}

async function renderBeacons() {
  const [beaconResult, artifactResult] = await Promise.all([
    request("/api/admin/beacons"),
    request("/api/admin/artifacts?lang=vi"),
  ]);
  cachedArtifacts = artifactResult.data;
  const options = cachedArtifacts.filter((artifact) => artifact.active)
    .map((artifact) => `<option value="${artifact.id}">${escapeHtml(artifact.code)} — ${escapeHtml(artifact.content?.title ?? "")}</option>`).join("");
  const rows = beaconResult.data.map((beacon) => `<tr>
    <td>${escapeHtml(beacon.beaconUuid)}</td><td>${beacon.major}/${beacon.minor}</td>
    <td>${escapeHtml(beacon.artifactCode ?? "Chưa gán")}</td><td>${escapeHtml(beacon.placement)}</td>
    <td><span class="badge">${beacon.active ? "Hoạt động" : "Đã ẩn"}</span></td>
    <td><button class="button danger" data-action="deactivate-beacon" data-id="${beacon.id}">Ẩn</button></td></tr>`);
  viewContainer.innerHTML = `
    <form id="beacon-form" class="form-card">
      <div class="section-heading"><h2>Đăng ký iBeacon</h2></div>
      <div class="form-grid">
        <label>UUID<input name="beaconUuid" placeholder="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx" required></label>
        <label>Hiện vật<select name="artifactId"><option value="">Chưa gán</option>${options}</select></label>
        <label>Major<input name="major" type="number" min="0" max="65535" value="1" required></label>
        <label>Minor<input name="minor" type="number" min="0" max="65535" value="1" required></label>
        <label>Vị trí lắp đặt<input name="placement" maxlength="255"></label>
        <label>Tx power<input name="txPower" type="number" min="-127" max="20" value="-59" required></label>
        <label>Vĩ độ<input name="latitude" type="number" step="any" min="-90" max="90"></label>
        <label>Kinh độ<input name="longitude" type="number" step="any" min="-180" max="180"></label>
      </div><div class="form-actions"><button class="button primary">Lưu beacon</button></div>
    </form>
    ${table(["UUID", "Major/Minor", "Hiện vật", "Vị trí", "Trạng thái", "Thao tác"], rows)}`;
}

async function renderFeedback() {
  const result = await request("/api/admin/feedback");
  const rows = result.data.map((item) => `<tr>
    <td>${escapeHtml(item.artifactCode)}</td><td>${escapeHtml(item.username)}</td>
    <td>${escapeHtml(item.rating)} / 5</td><td>${escapeHtml(item.comment)}</td>
    <td><span class="badge ${item.status === "PENDING" ? "pending" : ""}">${escapeHtml(item.status)}</span></td>
    <td>${item.status === "PENDING" ? `<div class="row-actions">
      <button class="button outline" data-action="approve-feedback" data-id="${item.id}">Duyệt</button>
      <button class="button danger" data-action="reject-feedback" data-id="${item.id}">Từ chối</button>
    </div>` : ""}</td></tr>`);
  viewContainer.innerHTML = table(["Hiện vật", "Tài khoản", "Điểm", "Nội dung", "Trạng thái", "Thao tác"], rows);
}

async function renderUsers() {
  const result = await request("/api/admin/users");
  const rows = result.data.map((user) => `<tr>
    <td>${escapeHtml(user.username)}</td><td>${escapeHtml(user.email)}</td><td>${escapeHtml(user.role)}</td>
    <td><span class="badge">${user.active ? "Đang hoạt động" : "Đã khóa"}</span></td>
    <td><button class="button ${user.active ? "danger" : "outline"}" data-action="toggle-user" data-id="${user.userId}" data-active="${!user.active}">${user.active ? "Khóa" : "Mở khóa"}</button></td></tr>`);
  viewContainer.innerHTML = table(["Tên người dùng", "Email", "Vai trò", "Trạng thái", "Thao tác"], rows);
}

async function renderLanguages() {
  const result = await request("/api/admin/languages");
  cachedLanguages = result.data;
  const rows = cachedLanguages.map((language) => `<tr>
    <td>${escapeHtml(language.code)}</td><td>${escapeHtml(language.name)}</td>
    <td><span class="badge">${language.active ? "Đang dùng" : "Đã ẩn"}</span></td>
    <td><button class="button danger" data-action="deactivate-language" data-id="${escapeHtml(language.code)}">Ẩn</button></td></tr>`);
  viewContainer.innerHTML = `
    <form id="language-form" class="form-card">
      <div class="section-heading"><h2>Thêm ngôn ngữ</h2></div>
      <div class="form-grid">
        <label>Mã ngôn ngữ<input name="code" maxlength="10" placeholder="en" required></label>
        <label>Tên ngôn ngữ<input name="name" maxlength="80" placeholder="English" required></label>
      </div><div class="form-actions"><button class="button primary">Lưu ngôn ngữ</button></div>
    </form>
    ${table(["Mã", "Tên", "Trạng thái", "Thao tác"], rows)}`;
}

function formValues(form) {
  return Object.fromEntries(new FormData(form).entries());
}

function nullableNumber(value) {
  return value === "" || value == null ? null : Number(value);
}

document.querySelector("#login-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = formValues(event.currentTarget);
  const error = document.querySelector("#login-error");
  error.textContent = "";
  try {
    const response = await fetch("/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(form),
    });
    const body = await response.json();
    if (!response.ok) throw new Error(body.message ?? "Không thể đăng nhập");
    if (body.data.role !== "ADMIN") throw new Error("Tài khoản không có quyền quản trị");
    sessionStorage.setItem(tokenKey, body.data.accessToken);
    showApp(body.data.username);
  } catch (reason) {
    error.textContent = reason.message;
  }
});

document.querySelector("#navigation").addEventListener("click", (event) => {
  const button = event.target.closest("[data-view]");
  if (button) loadView(button.dataset.view);
});

document.querySelector("#report-period").addEventListener("change", () => {
  if (currentView === "dashboard") loadView("dashboard");
});

document.querySelector("#logout-button").addEventListener("click", async () => {
  try {
    await request("/api/auth/logout", { method: "POST" });
  } catch (error) {
    showMessage(error.message, true);
  } finally {
    sessionStorage.removeItem(tokenKey);
    appShell.classList.add("hidden");
    loginScreen.classList.remove("hidden");
  }
});

document.querySelector("#view-container").addEventListener("submit", async (event) => {
  const form = event.target;
  event.preventDefault();
  try {
    const values = formValues(form);
    let path;
    let body;
    if (form.id === "site-form") {
      path = "/api/admin/sites";
      body = { ...values, latitude: nullableNumber(values.latitude), longitude: nullableNumber(values.longitude), active: true };
    } else if (form.id === "artifact-form") {
      path = "/api/admin/artifacts";
      body = { ...values, siteId: Number(values.siteId), active: true };
      delete body.imageFile;
      delete body.videoFile;
      delete body.audioFile;
      for (const [field, urlField] of [["imageFile", "imageUrl"], ["videoFile", "videoUrl"], ["audioFile", "audioUrl"]]) {
        const file = form.elements.namedItem(field).files[0];
        if (file) {
          const upload = new FormData();
          upload.append("file", file);
          const stored = await request("/api/admin/media", { method: "POST", body: upload });
          body[urlField] = stored.data.url;
        }
      }
    } else if (form.id === "beacon-form") {
      path = "/api/admin/beacons";
      body = {
        ...values, major: Number(values.major), minor: Number(values.minor), txPower: Number(values.txPower),
        artifactId: values.artifactId ? Number(values.artifactId) : null,
        latitude: nullableNumber(values.latitude), longitude: nullableNumber(values.longitude), active: true,
      };
    } else if (form.id === "language-form") {
      path = "/api/admin/languages";
      body = values;
    } else {
      return;
    }
    await request(path, { method: "POST", body: JSON.stringify(body) });
    showMessage("Đã lưu dữ liệu.");
    await loadView(currentView);
  } catch (error) {
    showMessage(error.message, true);
  }
});

document.querySelector("#view-container").addEventListener("click", async (event) => {
  const button = event.target.closest("[data-action]");
  if (!button) return;
  const id = button.dataset.id;
  try {
    switch (button.dataset.action) {
      case "deactivate-site": {
        const site = cachedSites.find((item) => String(item.id) === id);
        if (!site || !confirm(`Ẩn khu di tích “${site.name}”?`)) return;
        await request(`/api/admin/sites/${id}`, {
          method: "PUT",
          body: JSON.stringify({ ...site, active: false }),
        });
        break;
      }
      case "deactivate-artifact": {
        const artifact = cachedArtifacts.find((item) => String(item.id) === id);
        if (!artifact || !confirm(`Ẩn hiện vật “${artifact.code}”?`)) return;
        await request(`/api/admin/artifacts/${id}`, {
          method: "PUT",
          body: JSON.stringify({
            siteId: artifact.siteId, code: artifact.code, locationName: artifact.locationName,
            latitude: artifact.latitude, longitude: artifact.longitude, active: false,
            languageCode: artifact.content?.languageCode ?? "vi",
            title: artifact.content?.title ?? artifact.code,
            description: artifact.content?.description ?? "",
            imageUrl: artifact.content?.imageUrl ?? null,
            videoUrl: artifact.content?.videoUrl ?? null,
            audioUrl: artifact.content?.audioUrl ?? null,
          }),
        });
        break;
      }
      case "edit-translation": {
        const code = prompt("Mã ngôn ngữ (ví dụ: en):", "en");
        if (!code) return;
        const title = prompt("Tiêu đề bản dịch:");
        if (!title) return;
        const description = prompt("Nội dung bản dịch:");
        if (description === null) return;
        const artifact = cachedArtifacts.find((item) => String(item.id) === id);
        await request(`/api/admin/artifacts/${id}/content/${encodeURIComponent(code)}`, {
          method: "PUT",
          body: JSON.stringify({
            title, description,
            imageUrl: artifact?.content?.imageUrl ?? null,
            videoUrl: artifact?.content?.videoUrl ?? null,
            audioUrl: artifact?.content?.audioUrl ?? null,
          }),
        });
        break;
      }
      case "deactivate-beacon":
        await request(`/api/admin/beacons/${id}`, { method: "DELETE" });
        break;
      case "deactivate-language":
        await request(`/api/admin/languages/${encodeURIComponent(id)}`, { method: "DELETE" });
        break;
      case "approve-feedback":
      case "reject-feedback":
        await request(`/api/admin/feedback/${id}`, {
          method: "PATCH",
          body: JSON.stringify({ status: button.dataset.action === "approve-feedback" ? "APPROVED" : "REJECTED" }),
        });
        break;
      case "toggle-user":
        await request(`/api/admin/users/${id}/status`, {
          method: "PATCH",
          body: JSON.stringify({ active: button.dataset.active === "true" }),
        });
        break;
      default:
        return;
    }
    showMessage("Đã cập nhật dữ liệu.");
    await loadView(currentView);
  } catch (error) {
    showMessage(error.message, true);
  }
});

async function downloadReport(format) {
  try {
    const period = document.querySelector("#report-period").value;
    const response = await fetch(`/api/reports/export/${format}?period=${period}`, { headers: authHeader() });
    if (!response.ok) throw new Error("Không thể tạo báo cáo");
    const blob = await response.blob();
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = `smart-heritage-${period}.${format === "excel" ? "xlsx" : "pdf"}`;
    link.click();
    URL.revokeObjectURL(link.href);
  } catch (error) {
    showMessage(error.message, true);
  }
}

document.querySelector("#download-excel").addEventListener("click", () => downloadReport("excel"));
document.querySelector("#download-pdf").addEventListener("click", () => downloadReport("pdf"));

if (sessionStorage.getItem(tokenKey)) {
  request("/api/users/me").then((response) => {
    if (response.data.role !== "ADMIN") throw new Error("Tài khoản không có quyền quản trị");
    showApp(response.data.username);
  }).catch(() => {
    sessionStorage.removeItem(tokenKey);
    loginScreen.classList.remove("hidden");
  });
}
