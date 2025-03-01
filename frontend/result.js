
// 검색 버튼 클릭 이벤트
document.querySelector('.search-button').addEventListener('click', function() {
    const searchInput = document.getElementById('searchInput');
    if (searchInput.value.trim()) {
        // 검색어가 있으면 화살표 버튼 표시
        document.getElementById('showResultsBtn').style.display = 'flex';
    }
});

// 결과 보기 버튼 클릭 이벤트
document.getElementById('showResultsBtn').addEventListener('click', function() {
    const resultContainer = document.getElementById('result-container');
    resultContainer.classList.add('visible');

    // 부드러운 스크롤
    resultContainer.scrollIntoView({ behavior: 'smooth' });

    // 버튼 숨기기
    this.style.display = 'none';
});

// Enter 키로도 검색 가능하도록
document.getElementById('searchInput').addEventListener('keypress', function(e) {
    if (e.key === 'Enter' && this.value.trim()) {
        document.getElementById('showResultsBtn').style.display = 'flex';
    }
});