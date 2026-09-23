const { createClient } = require('@supabase/supabase-js');

async function test() {
  const url = "https://dylhcwjemoimazeprjbl.supabase.co";
  const key = "sb_publishable_hcXijsgeI7Jjjr74zltlcA_IkZyVnkf";
  const supabase = createClient(url, key);
  const { data, error } = await supabase.storage.getBucket('crescentconnect-files');
  console.log("Bucket Check:", { data, error });
  
  if (data) {
      // Try to upload a test file
      const { data: uploadData, error: uploadError } = await supabase.storage
        .from('crescentconnect-files')
        .upload('test-upload.txt', 'CrescentConnect Supabase Storage Test', {
            contentType: 'text/plain',
            upsert: true
        });
      console.log("Upload Test:", { uploadData, uploadError });
  }
}
test();
