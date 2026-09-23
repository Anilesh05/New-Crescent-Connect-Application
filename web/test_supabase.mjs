import { createClient } from '@supabase/supabase-js';
import fs from 'fs';
import { resolve } from 'path';

// read from web/.env.example or hardcode for test from earlier
const url = "https://dylhcwjemoimazeprjbl.supabase.co";
const key = "sb_publishable_hcXijsgeI7Jjjr74zltlcA_IkZyVnkf";

async function test() {
  const supabase = createClient(url, key);
  
  console.log("Checking bucket...");
  const { data: bucketData, error: bucketError } = await supabase.storage.getBucket('crescentconnect-files');
  console.log("Bucket Check:", { bucketData, bucketError });
  
  if (bucketData || (bucketError && bucketError.message.includes('row-level security'))) {
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
  }
}
test();
