// src/data/speedDialItems.js
import { useToast } from 'primevue/usetoast';
import { useUIStore } from '@/stores/uiStore';

const useSpeedDialItems = (router) => {
  const toast = useToast();
  const uiStore = useUIStore();

  return [
    {
      label: 'Add',
      icon: 'pi pi-pencil',
      command: () => {
        router.push('/schedule/board-post');
      },
    },
    {
      label: 'Update',
      icon: 'pi pi-refresh',
      command: () => {
        toast.add({
          severity: 'success',
          summary: 'Update',
          detail: 'Data Updated',
          life: 3000,
        });
      },
    },
    {
      label: 'Menu',
      icon: 'pi pi-bars',
      command: () => {
        console.log('Menu Command Triggered');
        uiStore.toggleSidebar();
        console.log('Sidebar visible state:', uiStore.visible);
      },
    },
    {
      label: 'Delete',
      icon: 'pi pi-trash',
      command: () => {
        toast.add({
          severity: 'error',
          summary: 'Delete',
          detail: 'Data Deleted',
          life: 3000,
        });
      },
    },
    {
      label: 'Vue Website',
      icon: 'pi pi-external-link',
      command: () => {
        window.location.href = 'https://vuejs.org/';
      },
    },
  ];
};

export default useSpeedDialItems;
