<template>
    <div class="table-container">
      <!-- 테이블 타이틀 -->
      <h2 class="table-title">{{ title }}</h2>
      <div class="table-info">
        <span class="total-members"><strong>{{ totalLabel }}: {{ data.length }}</strong></span>
        <button class="btn-invite" @click="$emit('action')">{{ actionLabel }}</button>
      </div>
  
      <!-- 검색 및 필터 -->
      <div class="filter-container">
        <select v-model="selectedSort" class="filter-dropdown">
          <option value="newest">최신순</option>
          <option value="oldest">오래된순</option>
        </select>
        <input
          type="text"
          v-model="searchQuery"
          :placeholder="searchPlaceholder"
          class="search-input"
        />
      </div>
  
      <!-- 테이블 -->
      <table class="custom-table">
        <thead>
          <tr>
            <th v-for="header in headers" :key="header.key">
              {{ header.label }}
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, rowIndex) in paginatedData" :key="rowIndex">
            <td v-for="header in headers" :key="header.key">
              <button
                v-if="header.key === 'activity'"
                class="btn-activity"
                @click="goToActivityPage(item)"
              >
                {{ item[header.key] }}
              </button>
              <span v-else>{{ item[header.key] }}</span>
            </td>
          </tr>
        </tbody>
      </table>
  
      <!-- 페이징 -->
      <div class="table-footer">
        <button @click="prevPage" :disabled="currentPage === 1">이전</button>
        <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
        <button @click="nextPage" :disabled="currentPage === totalPages">다음</button>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: "TableComponent",
    props: {
      title: { type: String, default: "테이블 제목" },
      headers: { type: Array, required: true },
      data: { type: Array, required: true },
      rowsPerPage: { type: Number, default: 10 },
      totalLabel: { type: String, default: "총 항목" },
      actionLabel: { type: String, default: "버튼 액션" },
      searchPlaceholder: { type: String, default: "검색" },
    },
    data() {
      return {
        currentPage: 1,
        searchQuery: "",
        selectedSort: "newest",
      };
    },
    computed: {
      filteredData() {
        return this.data.filter((item) =>
          Object.values(item).some((val) =>
            String(val).toLowerCase().includes(this.searchQuery.toLowerCase())
          )
        );
      },
      sortedData() {
        return this.filteredData.sort((a, b) => {
          if (this.selectedSort === "newest") {
            return new Date(b.joinDate) - new Date(a.joinDate);
          }
          return new Date(a.joinDate) - new Date(b.joinDate);
        });
      },
      totalPages() {
        return Math.ceil(this.sortedData.length / this.rowsPerPage);
      },
      paginatedData() {
        const start = (this.currentPage - 1) * this.rowsPerPage;
        return this.sortedData.slice(start, start + this.rowsPerPage);
      },
    },
    methods: {
      nextPage() {
        if (this.currentPage < this.totalPages) this.currentPage++;
      },
      prevPage() {
        if (this.currentPage > 1) this.currentPage--;
      },
      goToActivityPage(item) {
        alert(`${item.name || "항목"}의 활동 내역 페이지로 이동합니다.`);
      },
    },
  };
  </script>
  
  <style scoped>
  .table-container {
    width: 100%;
    margin: 20px auto;
    padding: 20px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  }
  
  .table-title {
    font-size: 24px;
    color: #414651;
    font-weight: bold;
    margin-bottom: 10px;
  }
  
  .table-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }
  
  .total-members {
    color: #414651;
    font-weight: bold;
    font-size: 16px;
  }
  
  .btn-invite {
    background-color: #7f56d9;
    color: white;
    padding: 8px 15px;
    border-radius: 5px;
    border: none;
    cursor: pointer;
  }
  
  .btn-invite:hover {
    background-color: #5a3b91;
  }
  
  .filter-container {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
  }
  
  .filter-dropdown,
  .search-input {
    padding: 8px;
    border: 1px solid #ddd;
    border-radius: 5px;
  }
  
  .custom-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 10px;
  }
  
  .custom-table th,
  .custom-table td {
    padding: 10px;
    text-align: left;
    color: #414651;
    border-bottom: 1px solid #ddd;
  }
  
  .custom-table th {
    background-color: #f4f4f4;
    font-weight: bold;
  }
  
  /* 활동 버튼 스타일 */
  .btn-activity {
    background-color: #7f56d9;
    color: white;
    padding: 5px 10px;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    opacity: 0.5;
  }
  
  .btn-activity:hover {
    opacity: 1;
  }
  
  .table-footer {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 20px;
  }
  
  .table-footer button {
    padding: 5px 10px;
    border: none;
    background-color: #7f56d9;
    color: white;
    border-radius: 5px;
    cursor: pointer;
    margin: 0 5px;
  }
  
  .table-footer button:disabled {
    background-color: #ddd;
    cursor: not-allowed;
  }
  
  .page-info {
    color: #414651;
  }
  </style>
  