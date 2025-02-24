function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    sidebar.classList.toggle('collapsed');
}

function selectCategory(button) {
    document.querySelectorAll('.category-button').forEach(btn => {
        btn.classList.remove('active');
    });
    button.classList.add('active');
}
window.toggleSidebar = toggleSidebar;
window.selectCategory = selectCategory;