import React, { useRef, useState } from "react";
import { EditorContent, useEditor } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
import Link from "@tiptap/extension-link";
import Image from "@tiptap/extension-image";
import TextAlign from "@tiptap/extension-text-align";
import Highlight from "@tiptap/extension-highlight";
import { Video } from "./admin/editor/Video";
import { uploadImage, uploadVideo } from "../api/cloudinaryApi";

// @ts-ignore
import "./richTextEditor.css";

interface RichTextEditorProps {
  value: string;
  onChange: (html: string) => void;
}

export function RichTextEditor({ value, onChange }: RichTextEditorProps) {
  const imageInputRef = useRef<HTMLInputElement | null>(null);
  const videoInputRef = useRef<HTMLInputElement | null>(null);

  const [uploadingImage, setUploadingImage] = useState(false);
  const [uploadingVideo, setUploadingVideo] = useState(false);

  const editor = useEditor({
    extensions: [
      StarterKit,
      Link.configure({
        openOnClick: false,
      }),
      Image.configure({
        inline: false,
        allowBase64: false,
      }),
      Highlight,
      TextAlign.configure({
        types: ["heading", "paragraph"],
      }),
    ],
    content: value,
    onUpdate({ editor }) {
      onChange(editor.getHTML());
    },
    extensions: [
      StarterKit,
      Link.configure({
        openOnClick: false,
      }),
      Image.configure({
        inline: false,
        allowBase64: false,
      }),
      Video,
      Highlight,
      TextAlign.configure({
        types: ["heading", "paragraph"],
      }),
    ],
  });

  if (!editor) return null;

  function setLink() {
    const previousUrl = editor.getAttributes("link").href;
    const url = window.prompt("Digite a URL do link:", previousUrl || "");

    if (url === null) return;

    if (url.trim() === "") {
      editor.chain().focus().unsetLink().run();
      return;
    }

    const normalizedUrl = url.trim();

    if (
        !normalizedUrl.startsWith("https://") &&
        !normalizedUrl.startsWith("http://") &&
        !normalizedUrl.startsWith("mailto:")
    ) {
      alert("Use uma URL começando com http://, https:// ou mailto:");
      return;
    }

    editor
        .chain()
        .focus()
        .setLink({
          href: normalizedUrl,
          target: "_blank",
          rel: "noopener noreferrer",
        })
        .run();
  }
  function addImageByUrl() {
    const url = window.prompt("Digite a URL da imagem:");

    if (!url) return;

    editor.chain().focus().setImage({ src: url.trim() }).run();
  }

async function handleVideoUpload(event: React.ChangeEvent<HTMLInputElement>) {
  const file = event.target.files?.[0];

  if (!file) return;

  if (!file.type.startsWith("video/")) {
    alert("Selecione apenas arquivos de vídeo.");
    return;
  }

  const maxSizeInMb = 50;
  const maxSizeInBytes = maxSizeInMb * 1024 * 1024;

  if (file.size > maxSizeInBytes) {
    alert(`O vídeo deve ter no máximo ${maxSizeInMb}MB.`);
    return;
  }

  try {
    setUploadingVideo(true);

    const videoUrl = await uploadVideo(file);

    editor.chain().focus().setVideo({
      src: videoUrl,
      type: file.type || "video/mp4",
    }).run();
  } catch (error) {
    console.error("Erro ao enviar vídeo:", error);
    alert("Não foi possível enviar o vídeo.");
  } finally {
    setUploadingVideo(false);

    if (videoInputRef.current) {
      videoInputRef.current.value = "";
    }
  }
}

  async function handleImageUpload(event: React.ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0];

    if (!file) return;

    if (!file.type.startsWith("image/")) {
      alert("Selecione apenas arquivos de imagem.");
      return;
    }

    const maxSizeInMb = 3;
    const maxSizeInBytes = maxSizeInMb * 1024 * 1024;

    if (file.size > maxSizeInBytes) {
      alert(`A imagem deve ter no máximo ${maxSizeInMb}MB.`);
      return;
    }

    try {
      setUploadingImage(true);

      const imageUrl = await uploadImage(file);

      editor.chain().focus().setImage({ src: imageUrl }).run();
    } catch (error) {
      console.error("Erro ao enviar imagem:", error);
      alert("Não foi possível enviar a imagem.");
    } finally {
      setUploadingImage(false);

      if (imageInputRef.current) {
        imageInputRef.current.value = "";
      }
    }
  }

  return (
      <div className="rich-editor">
        <div className="editor-toolbar editor-toolbar-sticky">
          <div className="toolbar-group">
            <button type="button" className={editor.isActive("bold") ? "active" : ""} onClick={() => editor.chain().focus().toggleBold().run()}>
              B
            </button>

            <button type="button" className={editor.isActive("italic") ? "active" : ""} onClick={() => editor.chain().focus().toggleItalic().run()}>
              I
            </button>

            <button type="button" className={editor.isActive("strike") ? "active" : ""} onClick={() => editor.chain().focus().toggleStrike().run()}>
              S
            </button>

            <button type="button" className={editor.isActive("highlight") ? "active" : ""} onClick={() => editor.chain().focus().toggleHighlight().run()}>
              Destaque
            </button>
          </div>

          <div className="toolbar-group">
            <button type="button" className={editor.isActive("heading", { level: 2 }) ? "active" : ""} onClick={() => editor.chain().focus().toggleHeading({ level: 2 }).run()}>
              H2
            </button>

            <button type="button" className={editor.isActive("heading", { level: 3 }) ? "active" : ""} onClick={() => editor.chain().focus().toggleHeading({ level: 3 }).run()}>
              H3
            </button>

            <button type="button" className={editor.isActive("bulletList") ? "active" : ""} onClick={() => editor.chain().focus().toggleBulletList().run()}>
              Lista
            </button>

            <button type="button" className={editor.isActive("orderedList") ? "active" : ""} onClick={() => editor.chain().focus().toggleOrderedList().run()}>
              1. Lista
            </button>
          </div>

          <div className="toolbar-group">
            <button type="button" onClick={() => editor.chain().focus().setTextAlign("left").run()}>
              Esq.
            </button>

            <button type="button" onClick={() => editor.chain().focus().setTextAlign("center").run()}>
              Centro
            </button>

            <button type="button" onClick={() => editor.chain().focus().setTextAlign("right").run()}>
              Dir.
            </button>
          </div>

          <div className="toolbar-group">
            <button type="button" onClick={setLink}>
              Link
            </button>

            <button type="button" onClick={addImageByUrl}>
              Imagem URL
            </button>

            <button type="button" disabled={uploadingImage} onClick={() => imageInputRef.current?.click()}>
              {uploadingImage ? "Enviando..." : "Upload imagem"}
            </button>

            <button
              type="button"
              disabled={uploadingVideo}
              onClick={() => videoInputRef.current?.click()}
            >
              {uploadingVideo ? "Enviando vídeo..." : "Upload vídeo"}
            </button>
          </div>

          <div className="toolbar-group">
            <button type="button" onClick={() => editor.chain().focus().undo().run()}>
              ↶
            </button>

            <button type="button" onClick={() => editor.chain().focus().redo().run()}>
              ↷
            </button>
          </div>

          <input
            ref={imageInputRef}
            type="file"
            accept="image/*"
            hidden
            onChange={handleImageUpload}
          />

          <input
            ref={videoInputRef}
            type="file"
            accept="video/*"
            hidden
            onChange={handleVideoUpload}
          />
        </div>

        <EditorContent editor={editor} className="editor-content" />
      </div>
  );
}