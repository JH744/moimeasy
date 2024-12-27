<template>
  <div class="card">
    <FileUpload
      name="files[]"
      url="/api/upload"
      customUpload="true"
      :multiple="true"
      accept="image/*"
      :maxFileSize="1000000"
      @select="onSelectedFiles"
      @upload="onTemplatedUpload($event)"
    >
      <!-- 파일업로드 헤더 -->
      <template
        #header="{ chooseCallback, uploadCallback, clearCallback, files }"
      >
        <div class="upload-header-box">
          <div class="flex gap-2">
            <!-- 파일 선택 버튼-->
            <Button
              @click="chooseCallback()"
              icon="pi pi-images"
              rounded
              outlined
              severity="secondary"
            ></Button>
            <!-- 업로드 버튼 -->
            <Button
              @click="uploadEvent(uploadCallback)"
              icon="pi pi-cloud-upload"
              rounded
              outlined
              severity="success"
              :disabled="!files || files.length === 0"
            ></Button>
            <!-- 취소 버튼 -->
            <Button
              @click="clearCallback()"
              icon="pi pi-times"
              rounded
              outlined
              severity="danger"
              :disabled="!files || files.length === 0"
            ></Button>
          </div>
          <!-- 진행바 -->
          <ProgressBar
            :value="totalSizePercent"
            :showValue="false"
            class="progressbar-style"
          >
          </ProgressBar>
          <span class="whitespace-nowrap">{{ totalSize }}B / 1Mb</span>
        </div>
      </template>
      <template
        #content="{
          files,
          uploadedFiles,
          removeUploadedFileCallback,
          removeFileCallback,
        }"
      >
        <!-- 선택된 파일들  -->
        <div class="flex flex-col gap-8 pt-4">
          <div v-if="files.length > 0" class="seleted-file-box">
            <h5>Pending</h5>
            <div class="flex flex-wrap gap-4">
              <div
                v-for="(file, index) of files"
                :key="file.name + file.type + file.size"
                class="p-8 rounded-border flex flex-col border border-surface items-center gap-4"
              >
                <div>
                  <img
                    role="presentation"
                    :alt="file.name"
                    :src="file.objectURL"
                    width="100"
                    height="50"
                  />
                </div>
                <span
                  class="font-semibold text-ellipsis max-w-60 whitespace-nowrap overflow-hidden"
                  >{{ file.name }}</span
                >
                <div>{{ formatSize(file.size) }}</div>
                <Badge value="Pending" severity="warn" />
                <Button
                  icon="pi pi-times"
                  @click="
                    onRemoveTemplatingFile(file, removeFileCallback, index)
                  "
                  outlined
                  rounded
                  severity="danger"
                />
              </div>
            </div>
          </div>
          <!-- 업로드된 파일들 -->
          <div v-if="uploadedFiles.length > 0" class="seleted-file-box">
            <h5>Completed</h5>
            <div class="flex flex-wrap gap-4">
              <div
                v-for="(file, index) of uploadedFiles"
                :key="file.name + file.type + file.size"
                class="p-8 rounded-border flex flex-col border border-surface items-center gap-4"
              >
                <div>
                  <img
                    role="presentation"
                    :alt="file.name"
                    :src="file.objectURL"
                    width="100"
                    height="50"
                  />
                </div>
                <span
                  class="font-semibold text-ellipsis max-w-60 whitespace-nowrap overflow-hidden"
                  >{{ file.name }}</span
                >
                <div>{{ formatSize(file.size) }}</div>
                <Badge value="Completed" class="mt-4" severity="success" />
                <Button
                  icon="pi pi-times"
                  @click="removeUploadedFileCallback(index)"
                  outlined
                  rounded
                  severity="danger"
                />
              </div>
            </div>
          </div>
        </div>
      </template>
      <!-- 파일 선택 전 -->
      <template #empty>
        <div class="flex items-center justify-center flex-col">
          <i class="pi pi-cloud-upload upload-logo" />
          <p class="mt-6 mb-0">Drag and drop files to here to upload.</p>
        </div>
      </template>
    </FileUpload>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { usePrimeVue } from 'primevue/config';
import { useToast } from 'primevue/usetoast';
import Badge from 'primevue/badge';
import ProgressBar from 'primevue/progressbar';
import '@/views/gallery/components/GalleryFileUploader.style.css';
import {
  ref as firebaseRef,
  uploadBytes,
  getDownloadURL,
} from 'firebase/storage';
import { firebaseStorage } from '@/firebase/firebaseConfig';
import axios from 'axios';

const $primevue = usePrimeVue();
const toast = useToast();
// 파일 업로드 관련 변수
const totalSize = ref(0);
const totalSizePercent = ref(0);
const files = ref([]);
const uploadedFiles = ref([]); // uploadedFiles 선언 및 초기화
// 파일 크기 단위 변환 함수 -> 파일크기 표시용
const formatSize = (bytes) => {
  const k = 1024;
  const dm = 3;
  const sizes = $primevue.config.locale.fileSizeTypes;

  if (bytes === 0) {
    return `0 ${sizes[0]}`;
  }

  const i = Math.floor(Math.log(bytes) / Math.log(k));
  const formattedSize = parseFloat((bytes / Math.pow(k, i)).toFixed(dm));

  return `${formattedSize} ${sizes[i]}`;
};

// 아래 부터 이벤트 핸들러 함수들

/**  파일 선택 시 호출 */
const onSelectedFiles = (event) => {
  //   console.log('선택된 파일들:', event.files);
  files.value = event.files;
  // 파일 크기 계산
  files.value.forEach((file) => {
    totalSize.value += parseInt(formatSize(file.size));
  });
  totalSizePercent.value = totalSize.value / 10;
};

/**  파일 목록 Clear */
const clearFiles = (clearCallback) => {
  clearCallback();
  totalSize.value = 0;
  totalSizePercent.value = 0;
};

/**  특정 파일 삭제 */
const onRemoveTemplatingFile = (file, removeFileCallback, index) => {
  removeFileCallback(index);
  // 사이즈 갱신
  totalSize.value -= parseInt(formatSize(file.size));
  totalSizePercent.value = totalSize.value / 10;
};

/**  업로드 버튼 클릭 시 (수동 업로드 로직) */
const uploadEvent = async (primevueUploadCallback) => {
  console.log('업로드 버튼 클릭');

  // 1) Firebase 업로드 → 2) 서버 전송
  try {
    // 업로드한 파일들의 다운로드 URL을 담을 배열
    const uploadedFileUrls = [];
    for (const file of files.value) {
      // Firebase 업로드
      const storageRef = firebaseRef(
        firebaseStorage,
        `moeimeasy/gallery/${file.name}`
      );
      await uploadBytes(storageRef, file);
      const downloadUrl = await getDownloadURL(storageRef);
      console.log('다운로드 URL:', downloadUrl);
      // 다운로드 URL을 배열에 저장
      uploadedFileUrls.push({
        fileName: file.name,
        downloadUrl,
      });
    }

    // 2) 서버에 다운로드 URL 전송
    const response = await axios.post('/api/v1/gallery', {
      files: uploadedFileUrls,
      moeimId: 1,
    });

    console.log('서버 저장 결과:', response.data);

    // *** 핵심 수정 부분 ***
    if (response.data) {
      console.log(files.value);
      const uploadedFilesData = uploadedFileUrls.map((uploadedFile) => ({
        name: uploadedFile.fileName,
        objectURL: uploadedFile.downloadUrl,
        size:
          files.value.find((f) => f.name === uploadedFile.fileName)?.size || 0, // size가 없을 경우 0으로 설정
        type:
          files.value.find((f) => f.name === uploadedFile.fileName)?.type || '', // type이 없을 경우 빈 문자열로 설정
      }));

      // FileUpload 컴포넌트의 uploadedFiles를 업데이트하는 올바른 방법
      primevueUploadCallback({ files: uploadedFilesData });
      uploadedFiles.value = uploadedFilesData; // uploadedFiles 업데이트
      console.log('primevueUploadCallback 호출됨', uploadedFilesData);
    }

    console.log('파일 업로드 완료');
    // 업로드 성공 Toast
    toast.add({
      group: 'positioned',
      severity: 'success',
      summary: 'Upload Successful',
      detail: '파일 업로드 및 서버 저장 완료',
      life: 3000,
    });
  } catch (error) {
    console.error('파일 업로드/서버 저장 중 오류:', error);
    toast.add({
      group: 'positioned',
      severity: 'error',
      summary: 'Upload Failed',
      detail: '파일 업로드 중 문제가 발생했습니다.',
      life: 3000,
    });
  }
};

// PrimeVue FileUpload의 @upload 이벤트 콜백
// (여기서는 업로드 완료 후의 후처리를 주로 담당)
const onTemplatedUpload = () => {
  console.log('업로드 완료');
  toast.add({
    group: 'positioned',
    severity: 'info',
    summary: 'All Done',
    detail: 'File(s) processed!',
    life: 2000,
  });
};
</script>
