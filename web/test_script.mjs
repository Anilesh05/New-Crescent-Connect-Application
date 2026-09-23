import { createClient } from '@supabase/supabase-js';

const url = process.env.SUPABASE_URL;
const key = process.env.SUPABASE_ANON_KEY;

async function test() {
  const supabase = createClient(url, key);
  
  console.log("Checking bucket...");
  const { data: bucketData, error: bucketError } = await supabase.storage.getBucket('crescentconnect-files');
  console.log("Bucket Check:", { bucketData, bucketError });
  
  if (bucketData || (bucketError && (bucketError.message.includes('row-level security') || bucketError.message.includes('Access denied')))) {
      console.log("Attempting upload...");
      const { data: uploadData, error: uploadError } = await supabase.storage
        .from('crescentconnect-files')
        .upload('materials/test-user/test-upload.txt', 'CrescentConnect Supabase Storage Test', {
            contentType: 'text/plain',
            upsert: true
        });
      console.log("Upload Test:", { uploadData, uploadError });
      
      if (uploadData) {
          console.log("Attempting to get public URL / signed URL...");
          const { data: signedUrlData, error: signedUrlError } = await supabase.storage
            .from('crescentconnect-files')
            .createSignedUrl('materials/test-user/test-upload.txt', 3600);
          console.log("Signed URL:", signedUrlData, signedUrlError);
      }
  } else {
      console.log("Failed to find bucket.");
  }
}
test();
