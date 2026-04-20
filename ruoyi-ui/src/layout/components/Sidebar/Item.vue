<script>
export default {
  name: 'MenuItem',
  functional: true,
  props: {
    icon: {
      type: String,
      default: ''
    },
    title: {
      type: String,
      default: ''
    }
  },
  render(h, context) {
    const { icon, title } = context.props
    const vnodes = []

    if (icon) {
      vnodes.push(<svg-icon icon-class={icon}/>)
    }

    // 折叠时显示的首字
    if (title) {
      const firstChar = title.charAt(0)
      vnodes.push(<span class='menu-first-char'>{firstChar}</span>)
    }

    // 展开时显示的完整标题
    if (title) {
      if (title.length > 5) {
        vnodes.push(<span class='menu-title-full' title={(title)}>{(title)}</span>)
      } else {
        vnodes.push(<span class='menu-title-full'>{(title)}</span>)
      }
    }
    return vnodes
  }
}
</script>
