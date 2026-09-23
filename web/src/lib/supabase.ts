import { createClient } from '@supabase/supabase-js';

const supabaseUrl = import.meta.env.VITE_SUPABASE_URL || 'https://dylhcwjemoimazeprjbl.supabase.co';
const supabaseAnonKey = import.meta.env.VITE_SUPABASE_ANON_KEY || 'sb_publishable_hcXijsgeI7Jjjr74zltlcA_IkZyVnkf';

export const supabase = createClient(supabaseUrl, supabaseAnonKey);
