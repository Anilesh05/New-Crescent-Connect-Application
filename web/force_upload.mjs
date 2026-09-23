import { createClient } from '@supabase/supabase-js';

const url = process.env.SUPABASE_URL;
const key = process.env.SUPABASE_ANON_KEY;

async function test() {
  const supabase = createClient(url, key);
  
  console.log("Attempting upload to crescentconnect-files...");
  const { data: uploadData, error: uploadError } = await supabase.storage
    .from('crescentconnect-files')
    .upload('materials/test-user/test-upload.txt', 'CrescentConnect Supabase Storage Test', {
        contentType: 'text/plain',
        upsert: true
    });
  console.log("Upload Test:", { uploadData, uploadError });
}
test();
