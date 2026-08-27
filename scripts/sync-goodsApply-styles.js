const fs = require('fs');
const refundPath = 'e:/workspace/spd-ui/src/views/gzOrder/refund/index.vue';
const goodsPath = 'e:/workspace/spd-ui/src/views/gzOrder/goodsApply/index.vue';
const refund = fs.readFileSync(refundPath, 'utf8');
const goods = fs.readFileSync(goodsPath, 'utf8');
const styleStart = refund.indexOf('<style scoped');
const styleEnd = refund.lastIndexOf('</style>') + 8;
let styles = refund.slice(styleStart, styleEnd);
styles = styles.replace(/gzOrder-refund-page/g, 'gzOrder-goodsApply-page');
const goodsStyleStart = goods.indexOf('<style scoped');
const goodsStyleEnd = goods.lastIndexOf('</style>') + 8;
const extraSupplier = [
  '',
  '.local-modal-content .apply-modal-query-panel .apply-modal-field--compact .header-field-supplier-readonly,',
  '.local-modal-content .apply-modal-query-panel .apply-modal-field--compact .header-field-select-compact,',
  '.local-modal-content .apply-modal-query-panel .apply-modal-form-row .apply-modal-field--compact .header-field-supplier-readonly,',
  '.local-modal-content .apply-modal-query-panel .apply-modal-form-row .apply-modal-field--compact .header-field-select-compact {',
  '  width: 162px !important;',
  '  max-width: 162px !important;',
  '}',
  '.local-modal-content .apply-modal-query-panel .form-item-header-billno ::v-deep .el-input__inner,',
  '.local-modal-content .apply-modal-query-panel .form-item-header-supplier ::v-deep .el-input__inner {',
  '  overflow: hidden;',
  '  text-overflow: ellipsis;',
  '  white-space: nowrap;',
  '}',
  '.local-modal-content .apply-modal-query-panel .apply-modal-form-row.apply-modal-row-third.el-row {',
  '  flex-wrap: nowrap;',
  '}',
  ''
].join('\n');
const marker = '.local-modal-content .apply-modal-query-panel .apply-modal-form-row .apply-modal-field--compact .el-input {';
const insertAt = styles.indexOf(marker);
if (insertAt > -1) {
  const endRule = styles.indexOf('}', insertAt) + 1;
  styles = styles.slice(0, endRule) + extraSupplier + styles.slice(endRule);
}
const updated = goods.slice(0, goodsStyleStart) + styles + goods.slice(goodsStyleEnd);
fs.writeFileSync(goodsPath, updated, 'utf8');
console.log('Styles synced successfully');
