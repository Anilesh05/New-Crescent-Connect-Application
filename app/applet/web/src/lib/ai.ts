import { GoogleGenerativeAI } from '@google/generative-ai';

const apiKey = import.meta.env.VITE_GEMINI_API_KEY || "YOUR_API_KEY";

export const fallbackResponse = {
  summary: "AI OFFLINE / UNAVAILABLE. The Gemini API key is not configured.",
  keyTopics: ["Unavailable"],
  importantConcepts: ["AI Disabled"],
  definitions: ["Offline: Not connected to an active AI service."],
  importantPoints: ["The API key is a placeholder.", "Live AI generation is unavailable."],
  examFocus: ["Review configured environment variables."],
  studyQuestions: ["Is the API key configured?"],
  studyPlan: ["Configure VITE_GEMINI_API_KEY", "Restart the build"]
};

export async function analyzeDocumentText(text: string): Promise<any> {
  if (apiKey === "YOUR_API_KEY" || !apiKey || apiKey.trim() === "") {
    console.warn("Using offline fallback AI response.");
    return fallbackResponse;
  }

  try {
    const genAI = new GoogleGenerativeAI(apiKey);
    const model = genAI.getGenerativeModel({ model: "gemini-1.5-flash" });
    
    // Restrict text size to avoid timeouts and excessive token usage
    const limitedText = text.substring(0, 100000);
    const wasTruncated = text.length > 100000;

    const prompt = `
You are an expert academic tutor analyzing a document.
Use ONLY the supplied document text. Do not invent facts that are not present in the document.

Please analyze the following text and provide a structured JSON response.
Return ONLY raw JSON in exactly this format, without markdown code blocks, backticks, or any other text:

{
  "summary": "A concise summary of the document (2-3 sentences).",
  "keyTopics": ["Topic 1", "Topic 2", "Topic 3"],
  "importantConcepts": ["Concept 1", "Concept 2"],
  "definitions": ["Definition 1: explanation", "Definition 2: explanation"],
  "importantPoints": ["Point 1", "Point 2"],
  "examFocus": ["Important area for exam 1", "Important area for exam 2"],
  "studyQuestions": ["Question 1?", "Question 2?"],
  "studyPlan": ["Step 1", "Step 2"]
}

Document Text:
${limitedText}
`;

    const result = await model.generateContent(prompt);
    const response = await result.response;
    const responseText = response.text();
    
    // Clean up possible markdown wrappers around JSON
    let cleanJsonStr = responseText.replace(/```json/gi, '').replace(/```/gi, '').trim();
    
    try {
      const parsed = JSON.parse(cleanJsonStr);
      if (wasTruncated) {
          parsed.summary = `[Note: Document was very large. Analysis is based on the first 100k characters.] ${parsed.summary || ''}`;
      }
      return parsed;
    } catch (parseError) {
      console.error("Failed to parse AI JSON response:", responseText);
      return {
          ...fallbackResponse,
          summary: "Error parsing AI response. Raw output:\n\n" + responseText
      };
    }

  } catch (error: any) {
    console.error("AI Analysis failed:", error);
    return {
      ...fallbackResponse,
      summary: `AI OFFLINE / UNAVAILABLE. Error: ${error.message}`
    };
  }
}
