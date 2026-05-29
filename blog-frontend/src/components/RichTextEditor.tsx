import { EditorContent, useEditor } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
import Link from "@tiptap/extension-link";
import Image from "@tiptap/extension-image";
import TextAlign from "@tiptap/extension-text-align";
import Highlight from "@tiptap/extension-highlight";
import "../pages/postDetailsPage/postDetailsPage.css";

interface RichTextEditorProps {
  value: string;
  onChange: (html: string) => void;
}

export function RichTextEditor({ value, onChange }: RichTextEditorProps) {
  const editor = useEditor({
    extensions: [
      StarterKit,
      Link.configure({
        openOnClick: false,
      }),
      Image,
      Highlight,
      TextAlign.configure({
        types: ["heading", "paragraph"],
      }),
    ],
    content: value,
    onUpdate({ editor }) {
      onChange(editor.getHTML());
    },
  });

  if (!editor) return null;

  function setLink() {
    const url = window.prompt("Digite a URL do link:");

    if (!url) return;

    editor.chain().focus().setLink({ href: url }).run();
  }

  function addImage() {
    const url = window.prompt("Digite a URL da imagem:");

    if (!url) return;

    editor.chain().focus().setImage({ src: url }).run();
  }

  return (
    <div className="rich-editor">
      <div className="editor-toolbar">
        <button type="button" onClick={() => editor.chain().focus().toggleBold().run()}>
          <b>N</b>
        </button>

        <button type="button" onClick={() => editor.chain().focus().toggleItalic().run()}>
          <i>I</i>
        </button>

        <button type="button" onClick={() => editor.chain().focus().toggleStrike().run()}>
          <s>S</s>
        </button>

        <button type="button" onClick={() => editor.chain().focus().toggleHighlight().run()}>
          <mark> Marcar </mark>
        </button>

        <button type="button" onClick={() => editor.chain().focus().toggleHeading({ level: 2 }).run()}>
          <b>T</b>
        </button>

        <button type="button" onClick={() => editor.chain().focus().toggleHeading({ level: 3 }).run()}>
          Subtítulo
        </button>

        <button type="button" onClick={() => editor.chain().focus().toggleBulletList().run()}>
          Lista
        </button>

        <button type="button" onClick={() => editor.chain().focus().toggleOrderedList().run()}>
          Numerada
        </button>

        <button type="button" onClick={() => editor.chain().focus().toggleBlockquote().run()}>
          Citação
        </button>

        <button type="button" onClick={() => editor.chain().focus().setHorizontalRule().run()}>
          Linha
        </button>

        <button type="button" onClick={() => editor.chain().focus().setTextAlign("left").run()}>
          Esquerda
        </button>

        <button type="button" onClick={() => editor.chain().focus().setTextAlign("center").run()}>
          Centro
        </button>

        <button type="button" onClick={() => editor.chain().focus().setTextAlign("right").run()}>
          Direita
        </button>

        <button type="button" onClick={setLink} className="transparent">
          <img src="../../link-icon.png" alt="link-icon" width = "20px"/>
        </button>

        <button type="button" onClick={addImage}>
          Imagem por URL
        </button>

        <button type="button" onClick={() => editor.chain().focus().undo().run()}>
          Desfazer
        </button>

        <button type="button" onClick={() => editor.chain().focus().redo().run()}>
          Refazer
        </button>
      </div>

      <EditorContent editor={editor} className="editor-content" />
    </div>
  );
}