import axios from 'axios';

export async function fetchAddNotification(header, body, moeimId) {
  console.log(
    '알림 추가 매소드 호출',
    header,
    '  바디:  ',
    body,
    '  모임아이디: ',
    moeimId
  );
  try {
    let notification = {
      header: header,
      body: body,
      moeimId: moeimId,
    };
    console.log('알림추가 매소드 실행전');
    const { data } = await axios.post('/api/v1/notifications', notification);
    console.log('알림 추가 응답결과: ', data);
  } catch (error) {
    console.error('알림 추가 실패:', error);
  }
}
