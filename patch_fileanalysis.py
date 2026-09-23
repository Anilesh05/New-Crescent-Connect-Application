import re

with open("web/src/pages/FileAnalysis.tsx", "r") as f:
    content = f.read()

target = """import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { UploadCloud, File, Search, BrainCircuit, FileText } from 'lucide-react';
import { supabase } from '../lib/supabase';

export const FileAnalysis = () => {
  const { userData } = useAuth();
  const [uploading, setUploading] = useState(false);
  const [files, setFiles] = useState<any[]>([
    { id: '1', name: 'Database_Concepts_Ch3.pdf', type: 'application/pdf', status: 'COMPLETED', size: '2.4 MB', date: 'Oct 12, 2023' },
    { id: '2', name: 'ML_Algorithm_Notes.docx', type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', status: 'PROCESSING', size: '1.1 MB', date: 'Oct 14, 2023' }
  ]);
  const [selectedFile, setSelectedFile] = useState<any>(null);

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file || !userData) return;
    
    setUploading(true);
    try {
      const filePath = `materials/${userData.id}/${file.name}`;
      
      const { data, error } = await supabase.storage
        .from('crescentconnect-files')
        .upload(filePath, file, {
          cacheControl: '3600',
          upsert: false
        });

      if (error) {
        throw error;
      }

      setFiles(prev => [{
        id: Date.now().toString(),
        name: file.name,
        type: file.type,
        status: 'PENDING',
        size: `${(file.size / (1024 * 1024)).toFixed(1)} MB`,
        date: 'Just now'
      }, ...prev]);
      
      setUploading(false);
    } catch (error) {
      console.error(error);
      setUploading(false);
      alert('Upload failed: ' + (error as Error).message);
    }
  };"""

replacement = """import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { UploadCloud, File, Search, BrainCircuit, FileText, Download } from 'lucide-react';
import { supabase } from '../lib/supabase';
import { db } from '../lib/firebase';
import { collection, query, where, addDoc, onSnapshot } from 'firebase/firestore';

export const FileAnalysis = () => {
  const { userData } = useAuth();
  const [uploading, setUploading] = useState(false);
  const [files, setFiles] = useState<any[]>([]);
  const [selectedFile, setSelectedFile] = useState<any>(null);

  useEffect(() => {
    if (!userData) return;
    const materialsRef = collection(db, 'study_materials');
    const q = query(materialsRef, where('staffId', '==', userData.id));
    const unsubscribe = onSnapshot(q, (snapshot) => {
      const fetchedFiles = snapshot.docs.map(doc => {
        const data = doc.data();
        return {
          id: doc.id,
          name: data.fileName || 'Unknown',
          type: data.fileType || 'application/octet-stream',
          status: data.syncStatus === 'COMPLETED' ? 'COMPLETED' : 'PENDING',
          size: 'N/A',
          date: data.createdAt ? new Date(data.createdAt).toLocaleDateString() : 'Just now',
          storagePath: data.storagePath
        };
      });
      setFiles(fetchedFiles);
    });
    return () => unsubscribe();
  }, [userData]);

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file || !userData) return;
    
    setUploading(true);
    try {
      const filePath = `materials/${userData.id}/${file.name}`;
      
      const { data, error } = await supabase.storage
        .from('crescentconnect-files')
        .upload(filePath, file, {
          cacheControl: '3600',
          upsert: false
        });

      if (error) {
        throw error;
      }

      const materialsRef = collection(db, 'study_materials');
      await addDoc(materialsRef, {
        courseId: 'general',
        staffId: userData.id,
        title: file.name,
        description: 'Uploaded via File Analysis',
        fileName: file.name,
        fileType: file.type,
        storagePath: filePath,
        createdAt: Date.now(),
        syncStatus: 'PENDING'
      });
      
      setUploading(false);
    } catch (error) {
      console.error(error);
      setUploading(false);
      alert('Upload failed: ' + (error as Error).message + (String(error).includes('security') ? '\\n\\n(Blocked by Supabase RLS)' : ''));
    }
  };

  const handleDownload = async (f: any) => {
    if (!f.storagePath) {
       alert("No storage path available.");
       return;
    }
    try {
       const { data, error } = await supabase.storage
         .from('crescentconnect-files')
         .createSignedUrl(f.storagePath, 3600);
       if (error) throw error;
       if (data?.signedUrl) {
           window.open(data.signedUrl, '_blank');
       }
    } catch (error) {
       console.error("Download error:", error);
       alert("Failed to generate download link: " + (error as Error).message);
    }
  };"""

content = content.replace(target, replacement)

# Add download button to UI
content = content.replace(
    '''<div className="shrink-0">
                {f.status === 'COMPLETED' && <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-emerald-100 text-emerald-700">Analyzed</span>}''',
    '''<div className="shrink-0 flex items-center gap-2">
                <button onClick={(e) => { e.stopPropagation(); handleDownload(f); }} className="text-slate-400 hover:text-blue-600 transition-colors">
                  <Download className="w-4 h-4" />
                </button>
                {f.status === 'COMPLETED' && <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-emerald-100 text-emerald-700">Analyzed</span>}'''
)

with open("web/src/pages/FileAnalysis.tsx", "w") as f:
    f.write(content)

