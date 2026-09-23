import { createClient } from '@supabase/supabase-js';

const url = "https://dylhcwjemoimazeprjbl.supabase.co";
const key = "sb_publishable_hcXijsgeI7Jjjr74zltlcA_IkZyVnkf";

async function run() {
  const supabase = createClient(url, key);
  console.log("Testing createBucket with anon key...");
  const { data, error } = await supabase.storage.createBucket('crescentconnect-files', {
    public: false,
    fileSizeLimit: 20971520
  });
  console.log("Result:", { data, error });
}
run();
