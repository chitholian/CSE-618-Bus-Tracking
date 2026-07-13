import { initializeApp } from 'firebase-admin/app';
import { getFirestore } from 'firebase-admin/firestore';
import { onValueUpdated } from 'firebase-functions/database';
initializeApp();

const firestore = getFirestore();

export const onUserStatusChanged = onValueUpdated(
  '/status/{uid}',
  async (event) => {
    const status = event.data.after.val();
    const ref = firestore.doc(`users/${event.params.uid}`);
    return ref.update({ online: status === 'online' });
  },
);
