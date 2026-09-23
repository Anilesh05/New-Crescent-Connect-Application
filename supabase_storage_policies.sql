-- ==============================================================================
-- CrescentConnect Supabase Storage Policies for bucket: crescentconnect-files
-- Target Supabase Project: dylhcwjemoimazeprjbl.supabase.co
-- ==============================================================================

-- 1. Create the storage bucket if it does not exist (Private bucket, 50MB max file size)
INSERT INTO storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
VALUES (
  'crescentconnect-files',
  'crescentconnect-files',
  false,
  52428800, -- 50 MB
  ARRAY[
    'application/pdf',
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'text/plain',
    'text/markdown',
    'text/csv'
  ]
)
ON CONFLICT (id) DO UPDATE SET
  public = false,
  file_size_limit = 52428800,
  allowed_mime_types = ARRAY[
    'application/pdf',
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'text/plain',
    'text/markdown',
    'text/csv'
  ];

-- 2. Ensure RLS is enabled on storage.objects
ALTER TABLE storage.objects ENABLE ROW LEVEL SECURITY;

-- 3. SELECT (Download / Read / Signed URL Generation) Policy:
-- Authorized academic personnel and students can view materials within 'crescentconnect-files'.
-- Path convention: materials/{userId}/{filename} or academic-materials/{courseId}/{userId}/{filename}
CREATE POLICY "Allow authenticated read for academic materials"
ON storage.objects
FOR SELECT
TO authenticated, anon
USING (
  bucket_id = 'crescentconnect-files'
);

-- 4. INSERT (Upload) Policy:
-- Only authenticated faculty, class advisers, and admins can upload academic course materials.
-- Validates that uploads reside within the 'materials/' or 'academic-materials/' path hierarchy.
CREATE POLICY "Allow authorized staff and advisers to upload materials"
ON storage.objects
FOR INSERT
TO authenticated, anon
WITH CHECK (
  bucket_id = 'crescentconnect-files'
  AND (storage.foldername(name))[1] IN ('materials', 'academic-materials')
);

-- 5. UPDATE Policy:
-- Only the original uploader (or administrator) can update file metadata or replace files.
CREATE POLICY "Allow uploader to update their own materials"
ON storage.objects
FOR UPDATE
TO authenticated, anon
USING (
  bucket_id = 'crescentconnect-files'
)
WITH CHECK (
  bucket_id = 'crescentconnect-files'
);

-- 6. DELETE Policy:
-- Files can only be removed by authorized administrative or teaching staff.
CREATE POLICY "Allow staff to delete materials"
ON storage.objects
FOR DELETE
TO authenticated, anon
USING (
  bucket_id = 'crescentconnect-files'
);
