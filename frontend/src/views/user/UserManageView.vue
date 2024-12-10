<template>
  <div class="member-container">
    <h2 class="table-title">회원 관리</h2>
    <div class="table-info">
      <span
        ><strong>총 회원 수: {{ filteredUsers.length }}</strong></span
      >
      <button class="btn-invite" @click="inviteMember">회원 초대</button>
    </div>

    <div class="filter-container">
      <select v-model="selectedSort" class="filter-dropdown">
        <option value="newest">최신순</option>
        <option value="oldest">오래된순</option>
      </select>
      <input
        type="text"
        v-model="searchQuery"
        placeholder="회원 검색"
        class="search-input"
      />
    </div>

    <table class="custom-table">
      <thead>
        <tr>
          <th v-for="header in headers" :key="header.key">
            {{ header.label }}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in paginatedUsers" :key="user.id">
          <td>
            <button class="btn-name" @click="viewProfile(user)">
              {{ user.name }}
              <!-- user.name은 매핑에서 정확히 확인 -->
            </button>
          </td>
          <td>{{ user.nickname }}</td>
          <!-- user.nickname이 제대로 나오는지 확인 -->
          <td>{{ user.role }}</td>
          <!-- user.role이 'user'나 'admin'으로 표시되는지 확인 -->
          <td>{{ user.joinDate }}</td>
          <!-- user.joinDate 확인 -->
        </tr>
      </tbody>
    </table>

    <div class="table-footer">
      <button @click="prevPage" :disabled="currentPage === 1">이전</button>
      <span>{{ currentPage }} / {{ totalPages }}</span>
      <button @click="nextPage" :disabled="currentPage === totalPages">
        다음
      </button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

axios.defaults.baseURL = 'http://localhost:8088';
axios.defaults.withCredentials = true;

export default {
  data() {
    return {
      headers: [
        { label: '이름', key: 'name' },
        { label: '닉네임', key: 'nickname' },
        { label: '권한', key: 'role' },
        { label: '가입 날짜', key: 'joinDate' },
      ],
      users: [],
      searchQuery: '',
      selectedSort: 'newest',
      currentPage: 1,
      rowsPerPage: 5,
    };
  },
  computed: {
    totalPages() {
      return Math.ceil(this.filteredUsers.length / this.rowsPerPage);
    },
    filteredUsers() {
      return this.users.filter((user) =>
        Object.values(user).some((val) =>
          String(val).toLowerCase().includes(this.searchQuery.toLowerCase())
        )
      );
    },
    sortedUsers() {
      return this.filteredUsers.sort((a, b) => {
        if (this.selectedSort === 'newest') {
          return new Date(b.joinDate) - new Date(a.joinDate);
        }
        return new Date(a.joinDate) - new Date(b.joinDate);
      });
    },
    paginatedUsers() {
      const start = (this.currentPage - 1) * this.rowsPerPage;
      return this.sortedUsers.slice(start, start + this.rowsPerPage);
    },
  },

  methods: {
    async fetchUsers() {
      try {
        const response = await axios.get('/api/v1/users', {
          params: { moeimId: this.loggedInUserMoeimId }, // 로그인된 사용자의 moeimId를 서버에 전달
        });
        console.log('Response data:', response.data);
        this.users = response.data.map((user) => ({
          id: user.userId, // userId가 맞는지 확인
          name: user.userName || 'N/A', // userName이 비어있는 경우 처리
          nickname: user.nickname || 'N/A', // nickname이 없을 경우 "N/A" 처리
          role: user.role || 'N/A', // role 데이터 확인
          joinDate: user.createAt || 'N/A', // joinDate를 위한 createAt 확인
        }));
        this.currentPage = 1;
        console.log('Mapped users:', this.users);
      } catch (error) {
        console.error('Error fetching users:', error);
      }
    },
    updateFilter() {
      this.fetchUsers(); // 필터 변경 시 데이터를 새로 가져오기
    },

    nextPage() {
      if (this.currentPage < this.totalPages) this.currentPage++;
    },
    prevPage() {
      if (this.currentPage > 1) this.currentPage--;
    },
    inviteMember() {
      this.$router.push({ name: 'InviteUser' });
    },
    viewProfile(user) {
      this.$router.push({ name: 'UserProfile', params: { userId: user.id } });
    },
  },
  watch: {
    selectedSort: 'updateFilter', // 필터 변경 시 데이터 업데이트
    searchQuery: 'updateFilter', // 검색어 변경 시 데이터 업데이트
  },
  mounted() {
    this.loggedInUserMoeimId = 3;
    this.fetchUsers();
  },
};
</script>

<style scoped>
.member-container {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.table-title {
  font-size: 24px;
  margin-bottom: 10px;
  font-weight: bold;
  color: #414651;
}

.table-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
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

.search-input,
.filter-dropdown {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 5px;
  color: #414651;
}

.custom-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
}

.custom-table th,
.custom-table td {
  padding: 10px;
  border: 1px solid #ddd;
  color: #414651;
}

.custom-table th {
  background-color: #f4f4f4;
  text-align: left;
}

.btn-name {
  background: none;
  color: #414651;
  border: none;
  cursor: pointer;
  text-decoration: underline;
}

.btn-name:hover {
  color: #7f56d9;
}

.table-footer {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.table-footer button {
  padding: 5px 10px;
  margin: 0 5px;
  background-color: #7f56d9;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.table-footer button:disabled {
  background-color: #ddd;
  cursor: not-allowed;
}
</style>
