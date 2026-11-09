const API = "";
const $ = (id) => document.getElementById(id);
let booksCache = [];

function notify(message, isError = false) {
  $("message").textContent = message;
  $("message").style.color = isError ? "#b33b3b" : "#26744d";
}
async function request(path, options = {}) {
  const response = await fetch(`${API}${path}`, {
    headers: { "Content-Type": "application/json", ...(options.headers || {}) },
    ...options
  });
  if (!response.ok) {
    let detail = `${response.status} ${response.statusText}`;
    try { const body = await response.json(); detail = body.message || body.error || detail; } catch {}
    throw new Error(detail);
  }
  if (response.status === 204) return null;
  return response.json();
}
function safe(value) {
  return String(value ?? "").replace(/[&<>"']/g, c => ({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[c]));
}
async function loadBooks(search = "") {
  booksCache = await request(`/api/books${search ? `?search=${encodeURIComponent(search)}` : ""}`);
  $("booksBody").innerHTML = booksCache.length ? booksCache.map(book => `
    <tr>
      <td><strong>${safe(book.title)}</strong><br><small>#${book.id}</small></td>
      <td>${safe(book.author)}</td><td>${safe(book.isbn || "—")}</td>
      <td>${safe(book.shelfCode || "—")}</td>
      <td><span class="status ${String(book.status).toLowerCase()}">${safe(book.status)}</span></td>
      <td><button class="delete" onclick="deleteBook(${book.id})">Delete</button></td>
    </tr>`).join("") : '<tr><td colspan="6" class="empty">No books found. Add your first book above.</td></tr>';
  $("bookCount").textContent = booksCache.length;
  $("availableCount").textContent = booksCache.filter(b => b.status === "AVAILABLE").length;
}
async function loadTasks() {
  const tasks = await request("/api/tasks");
  $("taskCount").textContent = tasks.length;
  $("pendingCount").textContent = tasks.filter(t => t.status === "PENDING").length;
  $("tasksList").innerHTML = tasks.length ? tasks.map(task => `
    <div class="task">
      <div><strong>#${task.id} · ${safe(task.taskType)}</strong>
      <small>${task.bookId ? `Book ID ${task.bookId} · ` : ""}${safe(task.destination || "No destination")} · ${new Date(task.createdAt).toLocaleString()}</small></div>
      <div class="task-actions">
        <span class="status ${task.status.toLowerCase()}">${safe(task.status)}</span>
        <select aria-label="Update task ${task.id}" onchange="updateTask(${task.id}, this.value)">
          ${["PENDING","IN_PROGRESS","COMPLETED","CANCELLED"].map(s => `<option ${task.status===s?"selected":""}>${s}</option>`).join("")}
        </select>
      </div>
    </div>`).join("") : '<p class="empty">No robot tasks have been created.</p>';
}
async function refresh() {
  try { await Promise.all([loadBooks($("search").value), loadTasks()]); }
  catch (e) { notify(`Cannot load data: ${e.message}. Check that Spring Boot and MySQL are running.`, true); }
}
$("bookForm").addEventListener("submit", async e => {
  e.preventDefault();
  const data = Object.fromEntries(new FormData(e.target));
  try {
    await request("/api/books", { method: "POST", body: JSON.stringify(data) });
    e.target.reset(); notify("Book added."); await refresh();
  } catch (err) { notify(err.message, true); }
});
$("taskForm").addEventListener("submit", async e => {
  e.preventDefault();
  const raw = Object.fromEntries(new FormData(e.target));
  const data = { taskType: raw.taskType, bookId: raw.bookId ? Number(raw.bookId) : null, destination: raw.destination || null };
  try {
    await request("/api/tasks", { method: "POST", body: JSON.stringify(data) });
    e.target.reset(); notify("Robot task created."); await refresh();
  } catch (err) { notify(err.message, true); }
});
$("search").addEventListener("input", () => loadBooks($("search").value).catch(e => notify(e.message, true)));
async function deleteBook(id) {
  if (!confirm("Delete this book?")) return;
  try { await request(`/api/books/${id}`, { method: "DELETE" }); notify("Book deleted."); await refresh(); }
  catch (e) { notify(e.message, true); }
}
async function updateTask(id, status) {
  try { await request(`/api/tasks/${id}/status`, { method: "PATCH", body: JSON.stringify({status}) }); notify("Task status updated."); await refresh(); }
  catch (e) { notify(e.message, true); }
}
refresh();
