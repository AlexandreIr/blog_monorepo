interface CloudinaryUploadResponse {
    secure_url: string;
    public_id: string;
    format: string;
    width: number;
    height: number;
    bytes: number;
}
// @ts-ignore
const CLOUD_NAME = import.meta.env.VITE_CLOUDINARY_CLOUD_NAME;
// @ts-ignore
const UPLOAD_PRESET: string = import.meta.env.VITE_CLOUDINARY_UPLOAD_PRESET;

export async function uploadImageToCloudinary(file: File): Promise<string> {
    if (!CLOUD_NAME && !UPLOAD_PRESET) {
        throw new Error("CLOUD_NAME e UPLOAD_PRESET variables not present");
    }
    if(!CLOUD_NAME){
        throw new Error("CLOUD_NAME variable not present");
    }
    if(!UPLOAD_PRESET){
        throw new Error("UPLOAD_PRESET variable not present");
    }

    const formData = new FormData();

    formData.append("file", file);
    formData.append("upload_preset", UPLOAD_PRESET);
    formData.append("folder", "blog-posts");

    const response = await fetch(
        `https://api.cloudinary.com/v1_1/${CLOUD_NAME}/image/upload`,
        {
            method: "POST",
            body: formData,
        }
    );

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Erro ao enviar imagem para o Cloudinary.");
    }

    const data = (await response.json()) as CloudinaryUploadResponse;

    return data.secure_url;
}