const COLORS = ['#7c3aed', '#db2777', '#ea580c', '#0891b2', '#16a34a', '#ca8a04', '#4f46e5', '#dc2626']

export function colorFor(name = '') {
  let h = 0
  for (const ch of name) h = (h * 31 + ch.charCodeAt(0)) >>> 0
  return COLORS[h % COLORS.length]
}

export default function Avatar({ name, size = 36 }) {
  const initials = name.split(/\s+/).map((p) => p[0]).join('').slice(0, 2).toUpperCase()
  return (
    <span
      aria-hidden="true"
      className="grid shrink-0 place-items-center rounded-full font-semibold text-white"
      style={{ width: size, height: size, background: colorFor(name), fontSize: size * 0.38 }}
    >
      {initials}
    </span>
  )
}
