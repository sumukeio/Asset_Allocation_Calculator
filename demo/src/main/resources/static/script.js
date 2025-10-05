// script.js

// 后端 API 地址，请确保与您的 Spring Boot 端口一致（默认 8080）
const API_BASE_URL = 'http://localhost:8080/api';

// 资产类型映射，用于前端展示和后端枚举匹配
const ASSET_TYPE_MAP = {
    'nasdaq': 'NASDAQ',
    'sp': 'SP',
    'conservative': 'CONSERVATIVE',
    'cash': 'CASH'
};

// 页面元素引用
const loadingOverlay = document.getElementById('loading-overlay');
const grandTotalDisplay = document.getElementById('grand-total');
const navConfig = document.getElementById('nav-config');
const navHistory = document.getElementById('nav-history');
const configView = document.getElementById('config-view');
const historyView = document.getElementById('history-view');
const calculateBtn = document.getElementById('calculate-btn');
const saveConfigBtn = document.getElementById('save-config-btn');
const assetForm = document.getElementById('add-asset-form');

// 图表实例 (需要全局引用以便销毁和重绘)
let recommendationChart = null;
let lineChart = null;
let pieChart = null;

// ==========================================================
// 启动与初始化
// ==========================================================

// 页面加载完成后调用初始化函数
document.addEventListener('DOMContentLoaded', initialize);

async function initialize() {
    // 检查后端连接并初始化数据
    await fetchAndRenderAllAssets();
    
    // 绑定事件
    navConfig.addEventListener('click', () => switchView('config'));
    navHistory.addEventListener('click', () => switchView('history'));
    calculateBtn.addEventListener('click', fetchRecommendation);
    saveConfigBtn.addEventListener('click', saveCurrentConfig);
    assetForm.addEventListener('submit', handleAssetFormSubmit);
}

// ==========================================================
// 导航与视图切换
// ==========================================================

function switchView(view) {
    configView.style.display = 'none';
    historyView.style.display = 'none';
    navConfig.classList.remove('active');
    navHistory.classList.remove('active');

    if (view === 'config') {
        configView.style.display = 'block';
        navConfig.classList.add('active');
    } else if (view === 'history') {
        historyView.style.display = 'block';
        navHistory.classList.add('active');
        fetchAndRenderHistory(); // 切换到历史记录时加载数据
    }
}


// ==========================================================
// 资产数据 CRUD (与后端 /api/assets 交互)
// ==========================================================

/**
 * 从后端获取所有资产并渲染到界面
 */
async function fetchAndRenderAllAssets() {
    showLoading('正在加载当前资产...');
    try {
        const response = await axios.get(`${API_BASE_URL}/assets`);
        const assets = response.data; // 后端返回的 List<AssetDetailDTO>
        
        // 1. 清空所有列表
        document.querySelectorAll('.asset-list').forEach(ul => ul.innerHTML = '');

        // 2. 统计总额并渲染列表
        let grandTotal = 0;
        const typeTotals = { nasdaq: 0, sp: 0, conservative: 0, cash: 0 };
        
        assets.forEach(item => {
            const typeKey = item.assetType.toLowerCase(); // NASDAQ -> nasdaq
            
            // 渲染列表项
            const ul = document.querySelector(`#block-${typeKey} .asset-list`);
            if (ul) {
                const li = document.createElement('li');
                li.innerHTML = `<span>${item.name}:</span> <span>${parseFloat(item.amount).toFixed(2)} 元</span>`;
                ul.appendChild(li);
            }
            
            // 累计总额
            typeTotals[typeKey] += parseFloat(item.amount);
            grandTotal += parseFloat(item.amount);
        });
        
        // 3. 更新区块总额和总资产显示
        for (const type in typeTotals) {
             document.querySelector(`.total-amount[data-asset="${type}"]`).textContent = typeTotals[type].toFixed(2);
        }
        grandTotalDisplay.textContent = grandTotal.toFixed(2);

    } catch (error) {
        console.error('获取资产数据失败 (请确保后端服务已启动!):', error);
        alert('连接后端服务失败或服务未启动。请检查控制台获取详情。');
    } finally {
        hideLoading();
    }
}

/**
 * 提交资产表单到后端
 */
async function handleAssetFormSubmit(e) {
    e.preventDefault();
    const typeKey = document.getElementById('asset-type-hidden').value;
    const type = ASSET_TYPE_MAP[typeKey]; // 转换为后端期望的类型（如 NASDAQ）
    const name = document.getElementById('asset-name').value;
    const amount = parseFloat(document.getElementById('asset-amount').value);

    if (!type || !name || amount <= 0) {
        alert('请输入有效的名称和金额！');
        return;
    }
    
    showLoading('正在添加资产...');
    try {
        const assetData = { assetType: type, name: name, amount: amount };
        await axios.post(`${API_BASE_URL}/assets`, assetData);
        
        closeModal();
        await fetchAndRenderAllAssets(); // 刷新列表和总额
        document.getElementById('recommendation-results').style.display = 'none'; // 隐藏旧的推荐结果
    } catch (error) {
        console.error('添加资产失败:', error);
        alert('添加资产失败，请重试。');
    } finally {
        hideLoading();
    }
}

// ==========================================================
// 推荐配置 (与后端 /api/calculate/recommendation 交互)
// ==========================================================

/**
 * 从后端获取推荐配置并渲染结果
 */
async function fetchRecommendation() {
    showLoading('正在计算推荐配置...');
    document.getElementById('recommendation-results').style.display = 'none';

    try {
        // 后端执行所有计算逻辑
        const response = await axios.get(`${API_BASE_URL}/calculate/recommendation`);
        const recommendation = response.data; // 后端返回的 RecommendationDTO
        
        renderRecommendationResults(recommendation);

    } catch (error) {
        console.error('计算推荐配置失败 (请检查后端逻辑):', error);
        alert('计算推荐配置失败，请确保您已添加资产。');
    } finally {
        hideLoading();
    }
}

/**
 * 渲染推荐结果表格和饼图
 * @param {Object} data - 后端返回的推荐数据
 */
function renderRecommendationResults(data) {
    const tbody = document.querySelector('#recommendation-table tbody');
    tbody.innerHTML = '';
    
    // 后端应该返回一个包含对比数据的列表，这里假设它是一个扁平对象
    const targetConfig = [
        { name: '纳指持仓', targetAmount: data.nasdaqTarget, currentAmount: data.nasdaqCurrent, targetRatio: data.nasdaqTargetRatio },
        { name: '标普持仓', targetAmount: data.spTarget, currentAmount: data.spCurrent, targetRatio: data.spTargetRatio },
        { name: '稳健投资', targetAmount: 0, currentAmount: data.conservativeCurrent, targetRatio: '0.00%' },
        { name: '现金', targetAmount: data.cashTarget, currentAmount: data.cashCurrent, targetRatio: data.cashTargetRatio },
    ];

    targetConfig.forEach(item => {
        const difference = item.targetAmount - item.currentAmount;
        const row = tbody.insertRow();
        row.insertCell().textContent = item.name;
        row.insertCell().textContent = item.currentAmount.toFixed(2) + ' 元';
        row.insertCell().textContent = `${item.targetAmount.toFixed(2)} 元 (${item.targetRatio})`;
        
        const diffCell = row.insertCell();
        diffCell.textContent = difference.toFixed(2) + ' 元';
        diffCell.style.color = difference > 0 ? 'green' : (difference < 0 ? 'red' : 'inherit');
    });

    document.getElementById('recommendation-results').style.display = 'block';
    
    // 绘制推荐饼图
    drawRecommendationChart(data);
}

/**
 * 绘制推荐配置饼图
 */
function drawRecommendationChart(data) {
    if (recommendationChart) recommendationChart.dispose();
    const chartDom = document.getElementById('recommendation-chart');
    recommendationChart = echarts.init(chartDom);
    
    const option = {
        title: { text: '推荐配置比例 (75/25)', left: 'center' },
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
        legend: { orient: 'vertical', left: 'left' },
        series: [{
            name: '推荐配置',
            type: 'pie',
            radius: '60%',
            data: [
                { value: data.nasdaqTarget, name: '纳指基金' },
                { value: data.spTarget, name: '标普基金' },
                { value: data.cashTarget, name: '现金' }
            ],
            emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } }
        }]
    };
    recommendationChart.setOption(option);
}


// ==========================================================
// 历史记录 (与后端 /api/records 交互)
// ==========================================================

/**
 * 保存当前配置到历史记录
 */
async function saveCurrentConfig() {
    showLoading('正在保存配置...');
    try {
        // 后端根据当前 asset_details 表的数据生成快照并存入 history_records 表
        await axios.post(`${API_BASE_URL}/records`); 
        alert('配置已成功保存！');
        // 保存后跳转到历史记录视图
        switchView('history'); 
    } catch (error) {
        console.error('保存配置失败:', error);
        alert('保存配置失败，请确保已计算推荐配置且后端服务运行正常。');
    } finally {
        hideLoading();
    }
}

/**
 * 获取并渲染历史记录
 */
async function fetchAndRenderHistory() {
    showLoading('正在加载历史记录...');
    try {
        const response = await axios.get(`${API_BASE_URL}/records`);
        const records = response.data; // List<HistoryRecordDTO>
        
        renderHistoryTable(records);
        
        if (records.length > 0) {
            renderHistoryCharts(records);
        } else {
            // 清空图表
            if(lineChart) lineChart.dispose();
            if(pieChart) pieChart.dispose();
        }
        
    } catch (error) {
        console.error('获取历史记录失败:', error);
        alert('获取历史记录失败，请检查后端服务。');
    } finally {
        hideLoading();
    }
}

/**
 * 渲染历史记录表格
 */
function renderHistoryTable(records) {
    const tbody = document.getElementById('history-table').querySelector('tbody');
    tbody.innerHTML = '';
    
    if (records.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5">暂无历史记录</td></tr>';
        return;
    }
    
    records.forEach(record => {
        const total = record.grandTotal;
        const row = tbody.insertRow();
        row.insertCell().textContent = new Date(record.recordDate).toLocaleDateString('zh-CN');
        row.insertCell().textContent = total.toFixed(2);
        row.insertCell().textContent = `${record.nasdaqTotal.toFixed(2)} (${(record.nasdaqTotal / total * 100).toFixed(1)}%)`;
        row.insertCell().textContent = `${record.spTotal.toFixed(2)} (${(record.spTotal / total * 100).toFixed(1)}%)`;
        row.insertCell().textContent = `${record.cashTotal.toFixed(2)} (${(record.cashTotal / total * 100).toFixed(1)}%)`;
    });
}

/**
 * 绘制历史记录图表
 */
function renderHistoryCharts(records) {
    // 确保数据顺序是从老到新，方便绘制趋势图
    const sortedRecords = [...records].reverse(); 

    // 绘制折线图
    if (lineChart) lineChart.dispose();
    const lineChartDom = document.getElementById('history-line-chart');
    lineChart = echarts.init(lineChartDom);
    
    const dates = sortedRecords.map(r => new Date(r.recordDate).toLocaleDateString('zh-CN', {month: 'short', day: 'numeric'}));
    const totals = sortedRecords.map(r => r.grandTotal);
    
    lineChart.setOption({
        title: { text: '总资产趋势', left: 'center' },
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: dates },
        yAxis: { type: 'value', name: '金额 (元)' },
        series: [{ name: '总资产', type: 'line', data: totals, smooth: true }]
    });
    
    // 绘制最新配置的饼图 (最新的在 records[0])
    if (pieChart) pieChart.dispose();
    const pieChartDom = document.getElementById('history-pie-chart');
    pieChart = echarts.init(pieChartDom);
    
    const latestRecord = records[0];
    pieChart.setOption({
        title: { text: `${new Date(latestRecord.recordDate).toLocaleDateString()} 实际配置饼图`, left: 'center' },
        series: [{
            name: '实际配置',
            type: 'pie',
            radius: '60%',
            data: [
                { value: latestRecord.nasdaqTotal, name: '纳指持仓' },
                { value: latestRecord.spTotal, name: '标普持仓' },
                { value: latestRecord.cashTotal, name: '现金' }
            ]
        }]
    });
}

// ==========================================================
// 辅助函数
// ==========================================================

function openModal(title, assetType) {
    document.getElementById('modal-title').textContent = `添加 ${title}`;
    document.getElementById('asset-type-hidden').value = assetType;
    document.getElementById('add-asset-modal').style.display = 'block';
}

function closeModal() {
    document.getElementById('add-asset-modal').style.display = 'none';
    assetForm.reset();
}

// 点击模态框外部关闭
window.onclick = function(event) {
    if (event.target === document.getElementById('add-asset-modal')) {
        closeModal();
    }
};

function showLoading(message) {
    loadingOverlay.textContent = message || '加载中...';
    loadingOverlay.style.display = 'flex';
}

function hideLoading() {
    loadingOverlay.style.display = 'none';
}