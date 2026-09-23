import mammoth from 'mammoth';
import * as pdfjsLib from 'pdfjs-dist';
import pdfWorkerUrl from 'pdfjs-dist/build/pdf.worker.min.mjs?url';

// Configure the worker for pdfjs-dist
pdfjsLib.GlobalWorkerOptions.workerSrc = pdfWorkerUrl;

export async function extractTextFromFile(file: File): Promise<string> {
  const fileType = file.type;
  const fileName = file.name.toLowerCase();

  try {
    if (fileType === 'text/plain' || fileName.endsWith('.txt') || fileName.endsWith('.md') || fileName.endsWith('.csv')) {
      return await file.text();
    } else if (fileType === 'application/pdf' || fileName.endsWith('.pdf')) {
      return await extractPdfText(file);
    } else if (
      fileType === 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' || 
      fileName.endsWith('.docx')
    ) {
      return await extractDocxText(file);
    } else {
      throw new Error(`Unsupported file format for extraction: ${fileName}`);
    }
  } catch (err: any) {
    throw new Error(`Extraction failed: ${err.message}`);
  }
}

async function extractPdfText(file: File): Promise<string> {
  const arrayBuffer = await file.arrayBuffer();
  const loadingTask = pdfjsLib.getDocument({ data: arrayBuffer });
  const pdf = await loadingTask.promise;
  
  let extractedText = '';
  for (let i = 1; i <= pdf.numPages; i++) {
    try {
      const page = await pdf.getPage(i);
      const content = await page.getTextContent();
      const pageText = content.items.map((item: any) => item.str).join(' ');
      extractedText += `\n--- Page ${i} ---\n${pageText}\n`;
    } catch (e) {
      console.warn(`Failed to extract text from page ${i}`, e);
    }
  }
  return extractedText;
}

async function extractDocxText(file: File): Promise<string> {
  const arrayBuffer = await file.arrayBuffer();
  const result = await mammoth.extractRawText({ arrayBuffer });
  return result.value;
}

export function normalizeText(text: string): string {
  if (!text) return '';
  // Remove excessive whitespace, replace multiple newlines with double newline
  let normalized = text.replace(/[\r\n]+/g, '\n\n');
  normalized = normalized.replace(/[ \t]+/g, ' ');
  return normalized.trim();
}
