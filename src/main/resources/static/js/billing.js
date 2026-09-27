const lineContainer = document.querySelector('#bill-lines');
const addLineButton = document.querySelector('#add-line');
const totalOutput = document.querySelector('#summary-total');
const countOutput = document.querySelector('#summary-count');
const billingForm = document.querySelector('#billing-form');
const currency = new Intl.NumberFormat('en-IN', {
  style: 'currency',
  currency: 'INR',
  minimumFractionDigits: 2,
});

if (lineContainer && addLineButton && totalOutput && countOutput && billingForm) {
  function updateSummary() {
    let total = 0;
    let count = 0;
    for (const line of lineContainer.querySelectorAll('.bill-line')) {
      const select = line.querySelector('select');
      const quantity = line.querySelector('input');
      const option = select.selectedOptions[0];
      const amount = Math.max(0, Number(quantity.value) || 0);
      quantity.max = option?.dataset.stock || '';
      total += Number(option?.dataset.price || 0) * amount;
      if (select.value) count += amount;
    }
    totalOutput.textContent = currency.format(total);
    countOutput.textContent = count;
  }

  function attachLineEvents(line) {
    line.querySelector('select').addEventListener('change', updateSummary);
    line.querySelector('input').addEventListener('input', updateSummary);
    line.querySelector('.remove-line').addEventListener('click', () => {
      if (lineContainer.querySelectorAll('.bill-line').length === 1) {
        line.querySelector('select').value = '';
        line.querySelector('input').value = '1';
      } else {
        line.remove();
      }
      updateSummary();
    });
  }

  attachLineEvents(lineContainer.querySelector('.bill-line'));
  addLineButton.addEventListener('click', () => {
    const line = lineContainer.querySelector('.bill-line').cloneNode(true);
    line.querySelector('select').value = '';
    line.querySelector('input').value = '1';
    lineContainer.append(line);
    attachLineEvents(line);
    updateSummary();
    line.querySelector('select').focus();
  });
  updateSummary();
}