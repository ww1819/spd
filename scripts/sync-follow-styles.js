const fs = require('fs');
const refundPath = 'e:/workspace/spd-ui/src/views/gzOrder/refund/index.vue';
const followPath = 'e:/workspace/spd-ui/src/views/gzOrder/follow/index.vue';
const refund = fs.readFileSync(refundPath, 'utf8');
const follow = fs.readFileSync(followPath, 'utf8');
const styleStart = refund.indexOf('<style scoped');
const styleEnd = refund.lastIndexOf('</style>') + 8;
let styles = refund.slice(styleStart, styleEnd);
styles = styles.replace(/gzOrder-refund-page/g, 'gzOrder-follow-page');
const followStyleStart = follow.indexOf('<style scoped');
const followStyleEnd = follow.lastIndexOf('</style>') + 8;
const extraRules = [
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
  '',
  '::v-deep .apply-modal-query-panel .form-item-order-date-no-star.is-required .el-form-item__label::before,',
  '::v-deep .apply-modal-query-panel .form-item-order-date-no-star.el-form-item--required .el-form-item__label::before {',
  '  display: none !important;',
  '  content: none !important;',
  '}',
  '',
  '.local-modal-content .apply-modal-table-panel .modal-entry-pagination {',
  '  flex-shrink: 0;',
  '  padding: 8px 12px;',
  '  border-top: 1px solid #e8ecf1;',
  '}',
  ''
].join('\n');
const marker = '.local-modal-content .apply-modal-query-panel .apply-modal-form-row .apply-modal-field--compact .el-input {';
const insertAt = styles.indexOf(marker);
if (insertAt > -1) {
  const endRule = styles.indexOf('}', insertAt) + 1;
  styles = styles.slice(0, endRule) + extraRules + styles.slice(endRule);
}
const updated = follow.slice(0, followStyleStart) + styles + follow.slice(followStyleEnd);
fs.writeFileSync(followPath, updated, 'utf8');
console.log('Follow styles synced successfully');
