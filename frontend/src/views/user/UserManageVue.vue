<template>
    <div class="member-container">
      <TableComponent
        title="회원 관리"
        :headers="headers"
        :data="users"
        :rowsPerPage="3"
        totalLabel="총 회원 수"
        actionLabel="회원 초대"
        searchPlaceholder="회원 검색"
        @action="inviteMember"
      />
    </div>
  </template>
  
  <script>
  import TableComponent from "@/components/TableComponent.vue";
  
  export default {
    components: { TableComponent },
    data() {
      return {
        headers: [
          { label: "이름", key: "name" },
          { label: "전화번호", key: "phone" },
          { label: "직책", key: "role" },
          { label: "활동 내역", key: "activity" },
          { label: "가입 날짜", key: "joinDate" },
        ],
        users: [
          { name: "이유진", phone: "010-1234-5678", role: "운영자", activity: "이동", joinDate: "2020-12-21" },
          { name: "장현수", phone: "010-2345-6789", role: "회원", activity: "이동", joinDate: "2020-12-23" },
          { name: "김지훈", phone: "010-3456-7890", role: "회원", activity: "이동", joinDate: "2020-12-22" },
          { name: "안성준", phone: "010-4567-8901", role: "회원", activity: "이동", joinDate: "2020-12-24" },
          { name: "이우진", phone: "010-5678-9012", role: "회원", activity: "이동", joinDate: "2020-12-21" },
        ],
      };
    },
    methods: {
      inviteMember() {
        this.$router.push({ name: "InviteUser" }); // InviteMember 컴포넌트로 이동
      },
    },
  };
  </script>
  
  <style scoped>
  .member-container {
    width: 400px;
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
  }
  </style>
  