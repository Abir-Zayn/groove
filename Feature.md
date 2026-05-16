# Groove — Feature Changelog

## Summarize Screen

### Summary Length — Keyboard Input
- Word count chip (right of "Summary Length" label) is now tappable
- Tapping opens a dialog with numeric keyboard input
- Accepts integers only
- Validates range 150–1000; shows Snackbar error if out of range: `"Number should be within 150 to 1000"`
- Slider range extended from 150–800 → 150–1000

### Markdown Preview
- Summary box renders LLM output as markdown instead of plain text
- Supported: `#` `##` `###` headings (bold + sized), `**bold**`, `*italic*` / `_italic_`, `- ` / `* ` bullets (→ `•`), `> ` blockquotes (italic), `---` horizontal rules
- Strips `<html>` tags from output

### Model Picker Component
- Dropdown added left of the Start Streaming button (50/50 row layout)
- Placeholder models: Llama 3.3 70B, Mistral 7B Instruct, Gemini Flash 1.5, DeepSeek R1, Qwen 2.5 72B
- Selected model shown with checkmark; state is local (mock UI — multi-model integration pending)

### Layout Restructure
- Model Picker + Start Streaming button moved below Summary Length slider in a side-by-side Row
- ModelPicker left (weight 1), StreamingButton right (weight 1)

### Streaming Debug Logs — tag: `GrooveStream` / `GrooveSSE`
- `GrooveStream` phases: `INIT` → `THINKING` → `EXECUTING` → `ACCELERATING` → `COMPLETE` / `CANCELLED` / `ERROR`
- `GrooveSSE` logs: HTTP code, headers, every raw SSE line, JSON parse errors, token + emit counts

### File Name Sanitization
- Spaces in uploaded filenames replaced with `_` at attach time (before navigation + display)
- Fixes empty streaming response caused by spaces in URI-encoded filename

### File Content Extraction
- `.docx`: extracts text from `word/document.xml` inside ZIP, strips XML tags
- `.pdf`: zero-dependency FlateDecode decompressor using `java.util.zip.Inflater`; parses `BT...ET` content stream blocks for `(string)Tj` / `[(str)]TJ` operators — converts binary PDF to clean text before sending to LLM
- `.txt` / `.md`: plain UTF-8 read (unchanged)

### Network & Model Fixes
- OkHttp timeouts: `connectTimeout=15s`, `writeTimeout=30s`, `readTimeout=120s` (was default 10s — killed SSE streams)
- Default model: `deepseek/deepseek-v4-flash:free` (confirmed working, supports streaming)
- Previous models tried and retired: `minimax/minimax-m2.5:free` (bad streaming), `meta-llama/llama-3.1-8b-instruct:free` (404), `mistralai/mistral-7b-instruct:free` (404)
