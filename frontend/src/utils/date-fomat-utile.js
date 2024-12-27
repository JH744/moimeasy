export default function formatDateTime(date) {
  if (!date) return '알 수 없음'; // date가 없는 경우 대비

  const parsedDate = new Date(date); // 문자열 형식의 날짜를 Date 객체로 변환

  // 날짜와 시간 형식 설정
  const year = parsedDate.getFullYear();
  const month = String(parsedDate.getMonth() + 1).padStart(2, '0'); // 월은 0부터 시작하므로 +1
  const day = String(parsedDate.getDate()).padStart(2, '0');

  let hours = parsedDate.getHours();
  const minutes = String(parsedDate.getMinutes()).padStart(2, '0');
  const ampm = hours >= 12 ? 'PM' : 'AM';
  hours = hours % 12 || 12; // 12시간제로 변환

  return `${year}.${month}.${day} ${ampm} ${hours}:${minutes}`;
}
