<template>
    <div class="total-frame">
        <div class="title-bar">
            <div class="title"> {{ currentMonth }} </div>
            <div class="icon-button-group">
                <i class="pi pi-caret-left icon-hover" @click="updateMonth(-1)"></i>
                <i class="pi pi-calendar-times"></i>
                <i class="pi pi-caret-right icon-hover" @click="updateMonth(1)"></i>
            </div>
            <div class="withdraw-money"> <Button label="회비사용" icon="pi pi-dollar" iconPos="right" rounded
                    class="expense-button" @click="expense = true" />
                <MoeimWithdraw v-model:visible="expense" :onFetch="fetchInitialData" />
            </div>
        </div>
        <div v-if="hasData">
            <Splitter>
                < <!-- 도넛 차트 -->
                    <SplitterPanel class="full-size-panel">
                        <div class="chart-wrapper">
                            <Chart type="doughnut" :data="chartData" :options="chartOptions" class="chart-container" />
                        </div>
                    </SplitterPanel>>
                    <SplitterPanel class="full-size-panel">
                        <div class="card datatable-card">
                            <DataTable :value="categoryData" virtualScroll scrollHeight="55%" ref="dt"
                                class="centered-datatable" tableStyle="min-width: 20rem">
                                <Column header="비율" class="header-style">
                                    <template #body="slotProps">
                                        <div class="circle-container">
                                            <div :style="{ backgroundColor: slotProps.data.categoryColor }"
                                                class="circle-content">
                                                <span>{{ slotProps.data.categoryPercentage }}%</span>
                                            </div>
                                        </div>
                                    </template>
                                </Column>
                                <Column :field="'categoryList'" header="항목" class="header-style">
                                    <template #body="slotProps">
                                        <span class="content">{{ slotProps.data.categoryList }}</span>
                                    </template>
                                </Column>
                                <Column :field="'categoryMoney'" header="금액" class="header-style">
                                    <template #body="slotProps">
                                        <!-- 숫자에 쉼표를 추가하고, 조건에 따라 클래스 지정 -->
                                        <Tag :value="slotProps.data.categoryMoney.toLocaleString()"
                                            :class="getCustomTagClass(slotProps.data.categoryMoney)" />
                                    </template>
                                </Column>
                            </DataTable>
                        </div>
                    </SplitterPanel>
            </Splitter>
        </div>
        <div v-else>
            <!-- 데이터가 없을 때 표시 -->
            <p>데이터가 없습니다.</p>
        </div>
        <ConfirmDialog />
    </div>
</template>

<script setup>
import '@/views/transaction/style/Transaction.style.css';
import '@/views/transaction/style/Button.style.css';
import '@/views/transaction/style/Modal.style.css';
import '@/views/transaction/style/Tag.style.css';
import 'primeicons/primeicons.css';
import { onMounted, ref } from "vue";
import Chart from 'primevue/chart';
import Button from 'primevue/button';
import Splitter from 'primevue/splitter';
import SplitterPanel from 'primevue/splitterpanel';
import DataTable from "primevue/datatable";
import Column from 'primevue/column';
import axios from "axios";
import { useRouter } from 'vue-router';
import { useLoadingStore } from '@/stores/useLoadingStore';
import MoeimWithdraw from "./MoeimWithdrawModal.vue";
import Tag from 'primevue/tag';
import { useConfirm } from "primevue/useconfirm"; // confirm 사용을 위해 추가


const loadingStore = useLoadingStore();
const router = useRouter();
const currentMonth = ref('');
const currentDate = ref(new Date()); // 현재 날짜
const hasData = ref(false); // 데이터 존재 여부
const chartOptions = ref({});
const categoryData = ref([]); // 백엔드에서 받아온 카테고리별 데이터
const chartData = ref({});
const expense = ref(false);

// 카테고리 개수에 따라 색상 배열 생성
const backgroundColors = [
    '#C19ED2', // Light Purple
    '#FFA2C3', // Light Pink
    '#FF7CA1', // Pink
    '#96C2EE', // Light Blue
    '#68A3EE', // Blue
    '#98f5e1', // Light Aqua
    '#FDE4CF', // Light Orange
    '#FFCFD2', // Light Coral
    '#F1C0E8', // Light Lavender
    '#B9FBC0'  // Light Green
];
const hoverBackgroundColors = [
    '#A678B9', // Darker Purple
    '#E67FA0', // Darker Pink
    '#E6737F', // Darker Pink
    '#7F99C9', // Darker Blue
    '#4F8CD3', // Darker Blue
    '#6ECFB8', // Darker Aqua
    '#E2C3B8', // Darker Orange
    '#E5A3B3', // Darker Coral
    '#D1A3E1', // Darker Lavender
    '#A3E0A0'  // Darker Green
];
const confirm = useConfirm(); // alert 창
const previousDate = ref(new Date()); // 이전 월
const isReverting = ref(false); // 되돌림 상태



// 백엔드로부터 초기 데이터 가져오기
async function fetchInitialData() {
    loadingStore.startLoading();  // 로딩 시작
    try {
        // Local Storage에서 accessToken, user 정보 가져오기
        const accessToken = localStorage.getItem('accessToken');
        const userRaw = localStorage.getItem('user');
        let userId = null;
        let moeimId = null;

        if (userRaw) {
            const user = JSON.parse(userRaw); // JSON 파싱
            userId = user?.userId || null; // 'userId' 키로 가져오기
            moeimId = user?.moeimId || null; // `moeimId` 키로 가져오기
        }

        if (!accessToken || !moeimId) {
            console.error('필수 데이터(accessToken 또는 moeimId)가 누락되었습니다.');
            router.push('/login'); // 로그인 화면으로 리디렉션
            return;
        }

        const startOfMonth = new Date(Date.UTC(currentDate.value.getFullYear(), currentDate.value.getMonth(), 1));
        const endOfMonth = new Date(Date.UTC(currentDate.value.getFullYear(), currentDate.value.getMonth() + 1, 0));

        const response = await axios.get("/api/v1/transaction/category", {
            headers: { Authorization: `Bearer ${accessToken}` },
            params: {
                userId: userId,
                startDate: startOfMonth.toISOString().split('T')[0],
                endDate: endOfMonth.toISOString().split('T')[0],
            },
        });
        if (Array.isArray(response.data.categories)) {
            categoryData.value = response.data.categories;
            hasData.value = categoryData.value.length > 0; // 데이터 존재 여부 설정
        } else {
            categoryData.value = [];
            hasData.value = false; // 데이터가 없을 경우
            console.error("categoryData가 응답에 없음");
        }

        //차트 데이터 설정
        chartData.value = setChartData();    // categoryData.value 기반으로 chartData 설정
        chartOptions.value = setChartOptions();
        transformChartData();                // chartData 기반 비율 계산
    }
    catch (error) {
        if (!isReverting.value) {
            isReverting.value = true;
            confirm1(error.response.data);
        }
        hasData.value = false; // 오류 발생 시 데이터가 없다고 설정
    }
    finally {
        loadingStore.stopLoading(); // 로딩 중지
    }
}

// 월 변경 및 데이터 다시 가져오기
const updateMonth = (offset) => {
    if (!isReverting.value) {
        previousDate.value = new Date(currentDate.value); // 이전 날짜 저장
        currentDate.value.setMonth(currentDate.value.getMonth() + offset);
        const month = currentDate.value.getMonth() + 1;
        currentMonth.value = `${currentDate.value.getMonth() + 1}월 카테고리별 지출 내용`;
        // 새로운 기간으로 데이터 재조회
        fetchInitialData();
    }
};

onMounted(() => {
    const date = new Date();
    currentMonth.value = `${date.getMonth() + 1}월 카테고리별 지출 내용`;
    fetchInitialData(); // 데이터 조회 후 chartData, categoryData 설정
});


// 금액에 따라 커스텀 클래스 결정
const getCustomTagClass = (amount) => {
    if (amount < 10000) {
        return 'custom-tag1';
    } else if (amount < 20000) {
        return 'custom-tag2';
    } else if (amount < 40000) {
        return 'custom-tag3';
    } else if (amount < 50000) {
        return 'custom-tag4';
    }
    else {
        return 'default';
    }
};


// chartData 설정: categoryData를 바탕으로 동적 차트 데이터 생성
const setChartData = () => {
    // categoryData.value에 따라 labels, data 동적 생성
    const labels = categoryData.value.map(item => item.categoryName);
    const amounts = categoryData.value.map(item => item.categoryMoney);

    // 카테고리 수가 5개보다 많을 수도 있으니 색상을 반복 사용
    const finalBgColors = labels.map((_, i) => backgroundColors[i % backgroundColors.length]);
    const finalHoverColors = labels.map((_, i) => hoverBackgroundColors[i % hoverBackgroundColors.length]);
    return {
        labels: labels,
        datasets: [
            {
                data: amounts,
                backgroundColor: finalBgColors,
                hoverBackgroundColor: finalHoverColors
            }
        ]
    };
};

// 차트 옵션 설정
const setChartOptions = () => {
    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--p-text-color');
    return {
        responsive: true,
        maintainAspectRatio: false,
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

// chartData를 바탕으로 categoryData에 비율 계산 속성 추가
const transformChartData = () => {
    if (chartData.value.datasets && chartData.value.datasets[0] && chartData.value.datasets[0].data) {
        const totalMoney = chartData.value.datasets[0].data.reduce((acc, curr) => acc + curr, 0); // double 합산
        categoryData.value = chartData.value.labels.map((label, index) => {
            const categoryMoney = chartData.value.datasets[0].data[index];
            const percentage = totalMoney > 0 ? ((categoryMoney / totalMoney) * 100).toFixed(2) : 0;
            return {
                categoryColor: chartData.value.datasets[0].backgroundColor[index],
                categoryList: label,
                categoryMoney: categoryMoney, // double 값 그대로
                categoryPercentage: percentage
            };
        });
    }
};

const confirm1 = (message) => {
    confirm.require({
        message: message,
        header: 'Error',
        icon: 'pi pi-times',
        rejectProps: {
            label: '취소',
            severity: 'secondary',
            outlined: true
        },
        acceptProps: {
            label: '확인'
        },
        accept: () => {
            // 이전 날짜로 되돌리기
            currentDate.value = previousDate.value;
            const month = currentDate.value.getMonth() + 1;
            currentMonth.value = `${month}월 지출내역`;
            isReverting.value = false;
            fetchInitialData(); // 이전 월 데이터 다시 가져오기
        },
        reject: () => {
        }
    });
};

</script>


<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap');

.full-size-panel,
.chart-wrapper {
    width: 90%;
    display: flex;
    align-items: center;
    height: 80vh;
    margin: 20px;
}

.datatable-card {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
}


.chart-container {
    width: 80%;
    display: flex;
    align-items: center;
    height: 80vh;
    margin: 20px;
}

.circle-container {
    position: relative;
    width: 7rem;
    height: 7rem;
}

.circle-content {
    width: 85%;
    height: 85%;
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
    color: white;
    font-size: 1.2rem;
}

:deep(span.p-datatable-column-title) {
    font-size: 1.5em !important;
    font-weight: bold !important;
    text-align: center !important;
}

/* 기본 태그 스타일 */
.p-tag {
    /* color: black !important; */
    font-size: 1rem !important;
    border-radius: 0.5rem !important;
    padding: 0.25rem 0.5rem !important;
    width: 35%;
    height: 40px;
}

.content {
    font-family: 'Noto Sans KR', sans-serif;
    font-size: 1.2rem;
    border-radius: 0.5rem;
    padding: 0.25rem 0.5rem;
    width: 80%;
    height: 40px;
    /* background-color: #f0f0f0; */
    /* 예시: 배경색 추가 */
    display: inline-block;
    /* 필요 시 추가 */
    /* color: #333; */
}

:deep(.p-tag) {
    height: 40px;
    width: 70px;
}
</style>
