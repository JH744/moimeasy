<template>
  <div>
  <div class="frame-427319196">
    <PayMenuList /> <!-- 메뉴 위치 -->
    <div class="frame-141">
      <div class="title">11월 회비 납부 내역</div>
      <Button label="납부하기" icon="pi pi-check" iconPos="right" rounded />
    </div>
  </div>
    <DataTable :value="members" paginator :rows="5" tableStyle="min-width: 60rem">
      <Column field="number" header="번호" style="width: 12%" />
      <Column header="사진" style="width: 12%" body="imageTemplate" />
      <Column field="name" header="이름" style="width: 12%" />
      <Column field="role" header="회원구분" style="width: 12%" />
      <Column field="amount" header="금액" style="width: 12%" />
      <Column field="payDate" header="납부일자" style="width: 12%" />
      <Column header="상태" style="width: 12%" body="statusTemplate" />
      <Column field="note" header="비고"  style="width: 20%"/>
    </DataTable>
  </div>
</template>

<script>
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Button from 'primevue/button';
import ColumnGroup from 'primevue/columngroup';   // optional
import Row from 'primevue/row';                   // optional
import PayMenuList from './PayMenuList.vue';

export default {
  name: 'PayList',
  components: {
    DataTable,
    Column,
    Button,
    ColumnGroup,
    Row,
    PayMenuList,
  },
  data() {
    return {
      members: [
        {
          number: '01',
          photo: 'avatar-image0.png', // 실제 이미지 경로로 변경
          name: '안성준',
          role: '운영자',
          amount: '50,000',
          payDate: '2024-11-30',
          status: '납부완료',
          note: '테스트입니다.'
        },
        {
          number: '02',
          photo: 'avatar-image1.png',
          name: '김지훈',
          role: '일반회원',
          amount: '50,000',
          payDate: '2024-11-30',
          status: '납부완료',
          note: ''
        },
        {
          number: '03',
          photo: 'avatar-image2.png',
          name: '장현수',
          role: '일반회원',
          amount: '50,000',
          payDate: '2024-11-30',
          status: '납부완료',
          note: ''
        },
        {
          number: '04',
          photo: 'avatar-image3.png',
          name: '이유진',
          role: '일반회원',
          amount: '50,000',
          payDate: '',
          status: '회비 미납',
          note: ''
        },
        {
          number: '05',
          photo: 'avatar-image4.png',
          name: '김철수',
          role: '일반회원',
          amount: '50,000',
          payDate: '',
          status: '회비 미납',
          note: ''
        }
      ]
    };
  },
  methods: {
    imageTemplate(member) {
      return h('img', { src: member.photo, alt: '사진', class: 'avatar-image' });
    },
    statusTemplate(member) {
      return h('span', { class: this.statusClass(member.status) }, member.status);
    }

  }
};
</script>

<style scoped>
.frame-427319196 {
  display: flex;
  flex-direction: column; /* 세로 방향으로 배치 */
  gap: 20px; /* 메뉴와 제목 간격 */
  align-items: flex-start; /* 왼쪽 정렬 */
  padding: 1rem;
}
:root {
  --p-datatable-border-color: black;
  --p-datatable-header-border-color: #9ca3af;
  --p-datatable-header-border-width: 2px;
  --p-datatable-body-cell-border-color: #e5e7eb;
  --p-datatable-header-color: #1f2937;
  --p-datatable-column-title-font-weight: bold;
}


.frame-141 {
  display: flex;
  justify-content: space-between; /* 왼쪽에 제목, 오른쪽에 버튼 */
  align-items: center; /* 수직 가운데 정렬 */
  padding-bottom: 2rem;
}

.title {
  font-size: 1.5rem;
  font-weight: bold;
}
</style>
