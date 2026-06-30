import { getToken } from "./adminApi";

// @ts-ignore
const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8081";

async function uploadFile(endpoint: string, file: File): Promise<string> {
  const token = getToken();

  if (!token) {
    throw new Error("Token de autenticação não encontrado. Faça login novamente.");
  }

  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch(`${API_URL}${endpoint}`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    body: formData,
  });

  if (!response.ok) {
    const errorBody = await response.text();

    console.error("Erro no upload:", {
      status: response.status,
      endpoint: `${API_URL}${endpoint}`,
      body: errorBody,
    });

    throw new Error(errorBody || "Erro ao enviar arquivo.");
  }

  const data = await response.json();

  if (!data.url) {
    throw new Error("A API não retornou a URL do arquivo.");
  }

  return data.url;
}

export function uploadImage(file: File): Promise<string> {
  return uploadFile("/admin/uploads/image", file);
}

export function uploadVideo(file: File): Promise<string> {
  return uploadFile("/admin/uploads/video", file);
}

export function uploadDocument(file: File): Promise<string> {
  return uploadFile("/admin/uploads/document", file);
}