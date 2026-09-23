import { createClient } from '@supabase/supabase-js';

const url = "https://dylhcwjemoimazeprjbl.supabase.co";
const key = "sb_publishable_hcXijsgeI7Jjjr74zltlcA_IkZyVnkf";

async function test() {
  console.log("Checking connection...");
  const supabase = createClient(url, key);
  
  const { data: bucketData, error: bucketError } = await supabase.storage.getBucket('crescentconnect-files');
  console.log("Bucket Check:", { bucketData, bucketError });
  
  if (bucketData || (bucketError && (bucketError.message.includes('row-level security') || bucketError.message.includes('Access denied')))) {
      console.log("Bucket found or access restricted by RLS (expected if public). Trying upload...");
      const { data: uploadData, error: uploadError } = await supabase.storage
        .from('crescentconnect-files')
        .upload('materials/test-user/test-upload.txt', 'CrescentConnect Supabase Storage Test', {
            contentType: 'text/plain',
            upsert: true
        });
      console.log("Upload Test:", { uploadData, uploadError });
      
      if (uploadData) {
          console.log("Upload success. Checking download...");
          const { data: signedUrlData, error: signedUrlError } = await supabase.storage
            .from('crescentconnect-files')
            .createSignedUrl('materials/test-user/test-upload.txt', 3600);
          console.log("Download URL generated:", signedUrlData);
      }
  } else {
      console.log("Failed to find bucket.");
  }
}
test();
