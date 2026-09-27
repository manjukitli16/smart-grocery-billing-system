const searchInput = document.querySelector('#inventory-search');
const inventoryRows = [...document.querySelectorAll('#inventory-table tbody tr')];
const noResults = document.querySelector('#no-results');

searchInput?.addEventListener('input', () => {
  const query = searchInput.value.trim().toLowerCase();
  let visibleCount = 0;
  for (const row of inventoryRows) {
    const visible = row.dataset.search.toLowerCase().includes(query);
    row.hidden = !visible;
    visibleCount += Number(visible);
  }
  if (noResults) noResults.hidden = visibleCount !== 0;
});

document.querySelectorAll('form[data-confirm]').forEach((form) => {
  form.addEventListener('submit', (event) => {
    if (!window.confirm(form.dataset.confirm)) event.preventDefault();
  });
});