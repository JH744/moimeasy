<template>
  <div class="board-wrapper">
    <div class="card">
      <DataTable
        v-model:filters="filters"
        :value="boards"
        paginator
        showGridlines
        :rows="10"
        dataKey="boardId"
        filterDisplay="menu"
        sortMode="single"
        :loading="loading"
        :globalFilterFields="['title', 'writerName', 'tag']"
      >
        <!-- 테이블 헤더 -->
        <template #header>
          <div class="filter-header">
            <Button
              type="button"
              icon="pi pi-filter-slash"
              label="Clear"
              outlined
              @click="clearFilter()"
            />
            <IconField>
              <InputIcon>
                <i class="pi pi-search" />
              </InputIcon>
              <InputText
                v-model="filters['global'].value"
                placeholder="Keyword Search"
              />
            </IconField>
          </div>
        </template>

        <!-- 번호 컬럼 -->
        <Column field="boardId" header="번호" style="min-width: 8rem" sortable>
          <template #body="{ data }">
            {{ data.boardId }}
          </template>
        </Column>

        <!-- 제목 컬럼 -->
        <Column field="title" header="제목" style="min-width: 14rem" sortable>
          <template #body="{ data }">
            <router-link :to="`/schedule/board-detail/${data.boardId}`">
              {{ data.title }}
            </router-link>
          </template>
        </Column>

        <!-- 작성자 컬럼 -->
        <Column
          field="writerName"
          header="작성자"
          style="min-width: 14rem"
          filterField="writerName"
          :showFilterMenu="false"
        >
          <template #body="{ data }">
            <div class="flex items-center gap-2">
              <img
                :src="data.profileUrl || defaultProfileImage"
                :alt="data.writerName"
                class="profile-image"
                style="width: 32px"
              />
              <span>{{ data.writerName }}</span>
            </div>
          </template>
          <template #filter="{ filterModel, filterCallback }">
            <MultiSelect
              v-model="filters['writerName'].value"
              @change="onWriterFilter"
              :options="userList"
              optionLabel="name"
              placeholder="작성자를 선택하세요"
              style="min-width: 14rem"
              :maxSelectedLabels="2"
            >
              <template #option="slotProps">
                <div class="flex items-center gap-2">
                  <img
                    :alt="slotProps.option.name"
                    :src="slotProps.option.image"
                    style="width: 32px"
                  />
                  <span>{{ slotProps.option.name }}</span>
                </div>
              </template>
            </MultiSelect>
          </template>
        </Column>
        <!-- 태그 컬럼 -->
        <Column
          field="tag"
          header="태그"
          :showFilterMenu="true"
          style="min-width: 12rem"
        >
          <template #body="{ data }">
            <Tag :value="data.tag" :severity="getSeverity(data.tag)" />
          </template>
          <template #filter="{ filterModel, filterCallback }">
            <Select
              v-model="filterModel.value"
              @change="filterCallback()"
              :options="tagOptions"
              placeholder="태그 선택"
              style="min-width: 12rem"
              :showClear="true"
            >
              <template #option="slotProps">
                <Tag
                  :value="slotProps.option"
                  :severity="getSeverity(slotProps.option)"
                />
              </template>
            </Select>
          </template>
        </Column>

        <!-- 게시일 컬럼 -->
        <Column
          field="createAt"
          header="게시일"
          style="min-width: 12rem"
          sortable
        >
          <template #body="{ data }">
            {{ formatDate(data.createAt) }}
          </template>
        </Column>
      </DataTable>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import Tag from 'primevue/tag';
import Select from 'primevue/select';
import MultiSelect from 'primevue/multiselect';
import IconField from 'primevue/iconfield';
import InputIcon from 'primevue/inputicon';
import { FilterMatchMode } from '@primevue/core/api';

const boards = ref([]);
const filters = ref();
const loading = ref(true);
// userList를 computed로 변경
const userList = computed(() => {
  // 중복 제거 및 고유한 작성자 목록 생성
  return [...new Set(boards.value.map((board) => board.writerName))].map(
    (name) => ({
      name: name,
      image:
        boards.value.find((board) => board.writerName === name)?.profileUrl ||
        defaultProfileImage,
    })
  );
});

const defaultProfileImage =
  'https://primefaces.org/cdn/primevue/images/avatar/amyelsner.png';

const tagOptions = ref(['info', 'success', 'warning', 'danger', '일반']); // 태그 옵션 예시

// 필터 초기화
const initFilters = () => {
  filters.value = {
    global: { value: null, matchMode: 'contains' },
    writerName: { value: null, matchMode: FilterMatchMode.IN },
    tag: { value: null, matchMode: FilterMatchMode.EQUALS },
  };
};
initFilters();

const getSeverity = (tag) => {
  const severities = ['info', 'success', 'warning', 'danger'];

  // 태그 문자열을 기반으로 일관된 랜덤 선택을 위해 해시 함수 사용
  const hashCode = (str) => {
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
      const char = str.charCodeAt(i);
      hash = (hash << 5) - hash + char;
      hash = hash & hash; // Convert to 32bit integer
    }
    return Math.abs(hash);
  };

  // 태그 문자열을 기반으로 인덱스 결정
  const index = hashCode(tag) % severities.length;
  return severities[index];
};

// 날짜 포맷 함수
const formatDate = (value) => {
  return new Date(value).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  });
};

// 작성자 필터 이벤트 핸들러
const onWriterFilter = (event) => {
  filters.value['writerName'].value = event.value;
};

// 게시판 데이터 가져오기
const fetchBoards = async () => {
  try {
    const response = await axios.get('/api/v1/boards');
    boards.value = response.data;
    // 데이터에서 고유한 태그 추출
    const uniqueTags = [...new Set(response.data.map((board) => board.tag))];
    tagOptions.value = uniqueTags;
  } catch (error) {
    console.error('Error fetching boards:', error);
  } finally {
    loading.value = false;
  }
};

// 필터 초기화 버튼
const clearFilter = () => {
  initFilters();
};

onMounted(() => {
  fetchBoards();
});
</script>

<style>
.board-wrapper {
  width: 98%;
  margin: 10px;
}

.filter-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
}

.profile-image {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: contain;
}

.p-datatable-table td {
  padding: 10px 16px !important;
  text-align: center;
  vertical-align: middle;
}
.p-datatable-table td:nth-child(3) {
  div {
    display: flex;
    align-items: center;
    gap: 12px;
    span,
    img {
      display: block;
    }
  }
}
/* .flex .items-center .gap-2 {
  display: flex;
  align-items: center;
} */
</style>
