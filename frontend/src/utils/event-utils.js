let eventGuid = 0;
let todayStr = new Date().toISOString().replace(/T.*$/, ''); // YYYY-MM-DD of today

export const INITIAL_EVENTS = [
  {
    id: createEventId(),
    title: 'All-day event',
    start: todayStr,
  },
  {
    id: createEventId(),
    title: 'Timed event',
    start: todayStr + 'T17:00:00',
  },
];
// 이벤트 고유아이디 생성
export function createEventId() {
  const timestamp = Date.now(); // 현재 시간 (밀리초 단위)
  const randomPart = Math.random().toString(36).slice(2, 11); // 난수에서 일부 문자열만 추출
  return `${timestamp}-${randomPart}`;
}
