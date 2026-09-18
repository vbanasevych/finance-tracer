document.getElementById('receiptInput').addEventListener('change', function(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const imgElement = document.getElementById('receiptPreview');
            imgElement.src = e.target.result;
            imgElement.classList.remove('d-none');
        };
        reader.readAsDataURL(file);
    }
});