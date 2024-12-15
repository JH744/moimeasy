<template>
    <div class="total-frame">
        <PayMenuList /> <!-- 메뉴 위치 -->
        <div class="title">모임명 : LEFT THREE TABLE</div>

        <!-- 제목과 아이콘을 왼쪽 아래로 배치 -->
        <div class="card" style="height: 100%; width: 100%;">
            <div class="sub-title" style="font-size: 1.5rem; font-weight: bold;">
                11월 거래내역
                <div class="icon-button-group">
                    <i class="pi pi-caret-left" style="font-size: 1rem"></i>
                    <i class="pi pi-calendar-times" style="font-size: 1rem"></i>
                    <i class="pi pi-caret-right" style="font-size: 1rem"></i>
                </div>
            </div>


            <Splitter style="width: 80%; height: 60vh; margin: 20px" class="mb-8">
                <SplitterPanel :size="50" :minSize="30" class="flex items-center justify-center full-size-panel">
                    <div class="chart-wrapper">
                        <Chart type="doughnut" :data="chartData" :options="chartOptions" class="chart-container"
                            style="width: 100%; height: 100%;" />
                    </div>
                </SplitterPanel>

                <SplitterPanel :size="50" :minSize="20" class="flex items-center justify-center full-size-panel">
                    <div class="card datatable-card">
                        <DataTable :value="transformedData" ref="dt" class="centered-datatable"
                            tableStyle="min-width: 20rem">
                            <Column header="비율">
                                <template #body="slotProps">
                                    <div style="position: relative; width: 6rem; height: 6rem;">
                                        <div :style="{
                                            backgroundColor: slotProps.data.categoryColor,
                                            width: '100%',
                                            height: '100%',
                                            borderRadius: '50%',
                                            display: 'flex',
                                            justifyContent: 'center',
                                            alignItems: 'center',
                                            color: 'white',
                                            fontSize: '1rem'
                                        }">
                                            <span>{{ slotProps.data.categoryPercentage }}%</span>
                                        </div>
                                    </div>
                                </template>
                            </Column>
                            <Column :field="'categoryList'" header="항목">
                                <template #body="slotProps">
                                    <span>{{ slotProps.data.categoryList }}</span>
                                </template>
                            </Column>
                            <Column :field="'categoryMoney'" header="금액">
                                <template #body="slotProps">
                                    <span>{{ slotProps.data.categoryMoney }}</span>
                                </template>
                            </Column>
                        </DataTable>
                    </div>
                </SplitterPanel>
            </Splitter>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import Chart from 'primevue/chart';
import PayMenuList from './PayMenuList.vue';
import Splitter from 'primevue/splitter';
import SplitterPanel from 'primevue/splitterpanel';
import DataTable from "primevue/datatable";
import Column from 'primevue/column';
import 'primeicons/primeicons.css';

onMounted(() => {
    chartData.value = setChartData();
    chartOptions.value = setChartOptions();
    transformChartData();  // chartData를 바탕으로 transformedData 생성
});

const chartData = ref({});
const chartOptions = ref({});
const transformedData = ref([]); // 차트 데이터를 DataTable 형식으로 변환한 데이터를 저장

// 1. 차트 데이터 설정
const setChartData = () => {
    const documentStyle = getComputedStyle(document.body);

    return {
        labels: ['식비', '다과비', '시설 대여비', '운영진 비용'],
        datasets: [
            {
                data: [240000, 350000, 640000, 350000],
                backgroundColor: [
                    documentStyle.getPropertyValue('--p-orange-500'),
                    documentStyle.getPropertyValue('--p-purple-500'),
                    documentStyle.getPropertyValue('--p-green-500'),
                    documentStyle.getPropertyValue('--p-blue-500')
                ],
                hoverBackgroundColor: [
                    documentStyle.getPropertyValue('--p-orange-400'),
                    documentStyle.getPropertyValue('--p-purple-400'),
                    documentStyle.getPropertyValue('--p-green-400'),
                    documentStyle.getPropertyValue('--p-blue-400')
                ]
            }
        ]
    };
};

// 2. 차트 옵션 설정
const setChartOptions = () => {
    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--p-text-color');

    return {
        responsive: true,  // 반응형으로 설정하여 크기 자동 조정
        maintainAspectRatio: false,  // 부모 요소의 크기에 맞추기 위해 비율 고정 해제
        plugins: {
            legend: {
                labels: {
                    color: textColor
                }
            }
        },
        cutout: '50%'
    };
};

// 3. 차트 데이터를 변환하여 DataTable에 표시할 형식으로 가공
const transformChartData = () => {
    const totalMoney = chartData.value.datasets[0].data.reduce((acc, curr) => acc + curr, 0); // 전체 금액 합산

    // 각 카테고리 금액 비율 계산
    if (chartData.value.datasets.length > 0) {
        transformedData.value = chartData.value.labels.map((label, index) => {
            const categoryMoney = chartData.value.datasets[0].data[index];
            const percentage = ((categoryMoney / totalMoney) * 100).toFixed(2); // 비율 계산 (소수점 2자리)
            return {
                categoryColor: chartData.value.datasets[0].backgroundColor[index],
                categoryList: label,
                categoryMoney: categoryMoney,
                categoryPercentage: percentage // 계산된 비율을 추가
            };
        });
    }
};
</script>

<style scoped>
.total-frame {
    display: flex;
    flex-direction: column;
    gap: 20px;
    align-items: flex-start;
    padding: 1rem;
    margin: 10px;
    height: 100%;
}

.title {
    font-size: 2em;
    font-weight: bold;
    width: 100%;
    display: flex;
    justify-content: center;
    position: relative;
    left: -150px;
    /* 텍스트를 살짝 왼쪽으로 이동 */
}

.sub-title {
    display: flex;
    align-items: center;
    /* 수직 정렬: 텍스트와 아이콘을 동일 선상에 배치 */
    justify-content: flex-start;
    position: relative;
    left: 300px;
    margin-top: 30px;
    margin-bottom: 30px;
    gap: 100px;
    /* 텍스트와 아이콘 간의 간격 */
}

.icon-button-group {
    display: flex;
    gap: 20px;
    /* 아이콘 간의 간격 */
}

.chart-container {
    width: 100%;
    height: 100%;
}

.full-size-panel {
    width: 100%;
    height: 100%;
}

.chart-wrapper {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>
