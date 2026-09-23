import { createClient } from '@supabase/supabase-js';

const url = process.env.SUPABASE_URL;
const key = process.env.SUPABASE_ANON_KEY;

async function test() {
  const supabase = createClient(url, key);
  
  console.log("Attempting list from crescentconnect-files...");
  const { data, error } = await supabase.storage
    .from('crescentconnect-files')
    .list();
  console.log("List Test:", { data, error });
}
test();
