import { ref, getDownloadURL } from 'firebase/storage';
import { firebaseStorage } from '@/firebase/firebaseConfig';

/**
 * Firebase Storage에서 다운로드 URL 가져오기
 * @param {string} filePath - Firebase Storage 내 파일 경로 (예: 'uploads/image.png')
 * @returns {Promise<string>} - 다운로드 가능한 이미지 URL 반환
 */
export const fetchImageUrl = async (filePath) => {
  try {
    const storageRef = ref(firebaseStorage, filePath);
    const downloadUrl = await getDownloadURL(storageRef);
    console.log('File path:', filePath);
    console.log('Firebase Storage instance:', firebaseStorage);
    console.log(' downloadUrl:', downloadUrl);
    return downloadUrl;
  } catch (error) {
    if (error.code === 'storage/object-not-found') {
      console.error(
        'Firebase Storage: 해당 경로에 파일이 존재하지 않습니다.',
        error
      );
    } else {
      console.error('Firebase Storage 이미지 URL 가져오기 실패:', error);
    }
    throw error; // 호출부에 오류를 던짐
  }
};
