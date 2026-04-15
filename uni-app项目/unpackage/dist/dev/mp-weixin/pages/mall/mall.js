(global["webpackJsonp"] = global["webpackJsonp"] || []).push([["pages/mall/mall"],{

/***/ 55:
/*!***********************************************************************************!*\
  !*** C:/Users/xyj/Desktop/green/uni-app项目/main.js?{"page":"pages%2Fmall%2Fmall"} ***!
  \***********************************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/* WEBPACK VAR INJECTION */(function(wx, createPage) {

var _interopRequireDefault = __webpack_require__(/*! @babel/runtime/helpers/interopRequireDefault */ 4);
__webpack_require__(/*! uni-pages */ 30);
var _vue = _interopRequireDefault(__webpack_require__(/*! vue */ 25));
var _mall = _interopRequireDefault(__webpack_require__(/*! ./pages/mall/mall.vue */ 56));
// @ts-ignore
wx.__webpack_require_UNI_MP_PLUGIN__ = __webpack_require__;
createPage(_mall.default);
/* WEBPACK VAR INJECTION */}.call(this, __webpack_require__(/*! ./node_modules/@dcloudio/uni-mp-weixin/dist/wx.js */ 1)["default"], __webpack_require__(/*! ./node_modules/@dcloudio/uni-mp-weixin/dist/index.js */ 2)["createPage"]))

/***/ }),

/***/ 56:
/*!****************************************************************!*\
  !*** C:/Users/xyj/Desktop/green/uni-app项目/pages/mall/mall.vue ***!
  \****************************************************************/
/*! no static exports found */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _mall_vue_vue_type_template_id_2e142186_scoped_true___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./mall.vue?vue&type=template&id=2e142186&scoped=true& */ 57);
/* harmony import */ var _mall_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./mall.vue?vue&type=script&lang=js& */ 59);
/* harmony reexport (unknown) */ for(var __WEBPACK_IMPORT_KEY__ in _mall_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_1__) if(["default"].indexOf(__WEBPACK_IMPORT_KEY__) < 0) (function(key) { __webpack_require__.d(__webpack_exports__, key, function() { return _mall_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_1__[key]; }) }(__WEBPACK_IMPORT_KEY__));
/* harmony import */ var _mall_vue_vue_type_style_index_0_id_2e142186_scoped_true_lang_css___WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./mall.vue?vue&type=style&index=0&id=2e142186&scoped=true&lang=css& */ 61);
/* harmony import */ var _Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_runtime_componentNormalizer_js__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../../../../../../../Hbuilders/HBuilderX/plugins/uniapp-cli/node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib/runtime/componentNormalizer.js */ 34);

var renderjs





/* normalize component */

var component = Object(_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_runtime_componentNormalizer_js__WEBPACK_IMPORTED_MODULE_3__["default"])(
  _mall_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_1__["default"],
  _mall_vue_vue_type_template_id_2e142186_scoped_true___WEBPACK_IMPORTED_MODULE_0__["render"],
  _mall_vue_vue_type_template_id_2e142186_scoped_true___WEBPACK_IMPORTED_MODULE_0__["staticRenderFns"],
  false,
  null,
  "2e142186",
  null,
  false,
  _mall_vue_vue_type_template_id_2e142186_scoped_true___WEBPACK_IMPORTED_MODULE_0__["components"],
  renderjs
)

component.options.__file = "pages/mall/mall.vue"
/* harmony default export */ __webpack_exports__["default"] = (component.exports);

/***/ }),

/***/ 57:
/*!***********************************************************************************************************!*\
  !*** C:/Users/xyj/Desktop/green/uni-app项目/pages/mall/mall.vue?vue&type=template&id=2e142186&scoped=true& ***!
  \***********************************************************************************************************/
/*! exports provided: render, staticRenderFns, recyclableRender, components */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_17_0_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_template_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_uni_app_loader_page_meta_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_mall_vue_vue_type_template_id_2e142186_scoped_true___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! -!../../../../../../../Hbuilders/HBuilderX/plugins/uniapp-cli/node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib/loaders/templateLoader.js??vue-loader-options!../../../../../../../Hbuilders/HBuilderX/plugins/uniapp-cli/node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--17-0!../../../../../../../Hbuilders/HBuilderX/plugins/uniapp-cli/node_modules/@dcloudio/webpack-uni-mp-loader/lib/template.js!../../../../../../../Hbuilders/HBuilderX/plugins/uniapp-cli/node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-uni-app-loader/page-meta.js!../../../../../../../Hbuilders/HBuilderX/plugins/uniapp-cli/node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!../../../../../../../Hbuilders/HBuilderX/plugins/uniapp-cli/node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!./mall.vue?vue&type=template&id=2e142186&scoped=true& */ 58);
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "render", function() { return _Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_17_0_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_template_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_uni_app_loader_page_meta_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_mall_vue_vue_type_template_id_2e142186_scoped_true___WEBPACK_IMPORTED_MODULE_0__["render"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "staticRenderFns", function() { return _Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_17_0_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_template_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_uni_app_loader_page_meta_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_mall_vue_vue_type_template_id_2e142186_scoped_true___WEBPACK_IMPORTED_MODULE_0__["staticRenderFns"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "recyclableRender", function() { return _Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_17_0_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_template_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_uni_app_loader_page_meta_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_mall_vue_vue_type_template_id_2e142186_scoped_true___WEBPACK_IMPORTED_MODULE_0__["recyclableRender"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "components", function() { return _Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_17_0_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_template_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_uni_app_loader_page_meta_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_mall_vue_vue_type_template_id_2e142186_scoped_true___WEBPACK_IMPORTED_MODULE_0__["components"]; });



/***/ }),

/***/ 58:
/*!***********************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************!*\
  !*** ./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--17-0!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/template.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-uni-app-loader/page-meta.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!C:/Users/xyj/Desktop/green/uni-app项目/pages/mall/mall.vue?vue&type=template&id=2e142186&scoped=true& ***!
  \***********************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************/
/*! exports provided: render, staticRenderFns, recyclableRender, components */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "render", function() { return render; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "staticRenderFns", function() { return staticRenderFns; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "recyclableRender", function() { return recyclableRender; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "components", function() { return components; });
var components
var render = function () {
  var _vm = this
  var _h = _vm.$createElement
  var _c = _vm._self._c || _h
  var g0 = !_vm.loadingCatalog ? _vm.filteredBenefits.length : null
  var g1 = !_vm.loadingRecords ? _vm.records.length : null
  _vm.$mp.data = Object.assign(
    {},
    {
      $root: {
        g0: g0,
        g1: g1,
      },
    }
  )
}
var recyclableRender = false
var staticRenderFns = []
render._withStripped = true



/***/ }),

/***/ 59:
/*!*****************************************************************************************!*\
  !*** C:/Users/xyj/Desktop/green/uni-app项目/pages/mall/mall.vue?vue&type=script&lang=js& ***!
  \*****************************************************************************************/
/*! no static exports found */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_mall_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! -!../../../../../../../Hbuilders/HBuilderX/plugins/uniapp-cli/node_modules/babel-loader/lib!../../../../../../../Hbuilders/HBuilderX/plugins/uniapp-cli/node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--13-1!../../../../../../../Hbuilders/HBuilderX/plugins/uniapp-cli/node_modules/@dcloudio/webpack-uni-mp-loader/lib/script.js!../../../../../../../Hbuilders/HBuilderX/plugins/uniapp-cli/node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!../../../../../../../Hbuilders/HBuilderX/plugins/uniapp-cli/node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!./mall.vue?vue&type=script&lang=js& */ 60);
/* harmony import */ var _Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_mall_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_mall_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0__);
/* harmony reexport (unknown) */ for(var __WEBPACK_IMPORT_KEY__ in _Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_mall_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0__) if(["default"].indexOf(__WEBPACK_IMPORT_KEY__) < 0) (function(key) { __webpack_require__.d(__webpack_exports__, key, function() { return _Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_mall_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0__[key]; }) }(__WEBPACK_IMPORT_KEY__));
 /* harmony default export */ __webpack_exports__["default"] = (_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_mall_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0___default.a); 

/***/ }),

/***/ 60:
/*!************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************!*\
  !*** ./node_modules/babel-loader/lib!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--13-1!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/script.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!C:/Users/xyj/Desktop/green/uni-app项目/pages/mall/mall.vue?vue&type=script&lang=js& ***!
  \************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/* WEBPACK VAR INJECTION */(function(uni) {

var _interopRequireDefault = __webpack_require__(/*! @babel/runtime/helpers/interopRequireDefault */ 4);
Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;
var _regenerator = _interopRequireDefault(__webpack_require__(/*! @babel/runtime/regenerator */ 41));
var _asyncToGenerator2 = _interopRequireDefault(__webpack_require__(/*! @babel/runtime/helpers/asyncToGenerator */ 43));
var _defineProperty2 = _interopRequireDefault(__webpack_require__(/*! @babel/runtime/helpers/defineProperty */ 11));
var _toConsumableArray2 = _interopRequireDefault(__webpack_require__(/*! @babel/runtime/helpers/toConsumableArray */ 18));
var _request = __webpack_require__(/*! ../../utils/request.js */ 44);
function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); enumerableOnly && (symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; })), keys.push.apply(keys, symbols); } return keys; }
function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = null != arguments[i] ? arguments[i] : {}; i % 2 ? ownKeys(Object(source), !0).forEach(function (key) { (0, _defineProperty2.default)(target, key, source[key]); }) : Object.getOwnPropertyDescriptors ? Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)) : ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } return target; }
var BottomNav = function BottomNav() {
  __webpack_require__.e(/*! require.ensure | components/bottom-nav */ "components/bottom-nav").then((function () {
    return resolve(__webpack_require__(/*! ../../components/bottom-nav.vue */ 168));
  }).bind(null, __webpack_require__)).catch(__webpack_require__.oe);
};
var _default = {
  name: 'MallPage',
  components: {
    BottomNav: BottomNav
  },
  data: function data() {
    return {
      username: '',
      userPoints: 0,
      benefits: [],
      records: [],
      activeCategory: 'ALL',
      activeBenefit: null,
      redeeming: false,
      loadingCatalog: false,
      loadingRecords: false,
      particleStyles: [],
      sparkleIcon: "\u2728",
      arrowIcon: "\u2192",
      closeIcon: 'x',
      defaultBenefitIcon: "\u2728",
      texts: {
        pageTitle: "\u7EFF\u8272\u6743\u76CA\u4E2D\u5FC3",
        pageSubtitle: "\u79EF\u5206\u53EF\u5728\u9910\u996E\u3001\u5B66\u4E60\u3001\u8363\u8A89\u573A\u666F\u4E2D\u771F\u5B9E\u5151\u73B0",
        catalogTitle: "\u53EF\u5151\u6362\u6743\u76CA",
        catalogSubtitle: "\u53EA\u4FDD\u7559\u5BF9\u6821\u56ED\u573A\u666F\u6709\u771F\u5B9E\u610F\u4E49\u7684\u6743\u76CA",
        recordTitle: "\u6211\u7684\u6838\u9500\u8BB0\u5F55",
        recordSubtitle: "\u5151\u6362\u540E\u53EF\u6309\u6307\u5F15\u5B8C\u6210\u6838\u9500\u6216\u67E5\u770B\u751F\u6210\u7ED3\u679C",
        sponsorLabel: "\u63D0\u4F9B\u65B9\uFF1A",
        deliveryLabel: "\u53D1\u653E\u65B9\u5F0F\uFF1A",
        redeemHintTitle: "\u9886\u53D6\u6307\u5F15",
        pointUnit: "\u79EF\u5206",
        soldOut: "\u5DF2\u5151\u5B8C",
        loading: "\u52A0\u8F7D\u4E2D...",
        emptyBenefits: "\u5F53\u524D\u573A\u666F\u6682\u65E0\u53EF\u7528\u6743\u76CA",
        emptyBenefitsHint: "\u53EF\u4EE5\u5207\u6362\u5230\u5176\u4ED6\u573A\u666F\u67E5\u770B",
        emptyRecords: "\u8FD8\u6CA1\u6709\u6743\u76CA\u8BB0\u5F55",
        emptyRecordsHint: "\u5B8C\u6210\u4E00\u6B21\u5151\u6362\u540E\uFF0C\u8FD9\u91CC\u4F1A\u663E\u793A\u76F8\u5E94\u6838\u9500\u4FE1\u606F",
        close: "\u5173\u95ED",
        detailAction: "\u67E5\u770B\u8BE6\u60C5",
        redeeming: "\u63D0\u4EA4\u4E2D...",
        redeemNow: "\u7ACB\u5373\u5151\u6362",
        soldOutAction: "\u5E93\u5B58\u4E0D\u8DB3",
        lackPoints: "\u79EF\u5206\u4E0D\u8DB3",
        confirmRedeemTitle: "\u786E\u8BA4\u5151\u6362",
        confirmRedeemPrefix: "\u5C06\u4F7F\u7528 ",
        confirmRedeemMiddle: ' ',
        confirmRedeemSuffix: "\u5151\u6362",
        redeemSuccessTitle: "\u5151\u6362\u6210\u529F",
        redeemSuccessView: "\u53BB\u67E5\u770B",
        redeemSuccessDone: "\u7A0D\u540E\u518D\u8BF4",
        badgeSuccessHint: "\u5FBD\u7AE0\u5DF2\u751F\u6210\uFF0C\u53EF\u5728\u9996\u9875\u663E\u8457\u4F4D\u7F6E\u67E5\u770B",
        redeemFailSoldOut: "\u5F53\u524D\u6743\u76CA\u5DF2\u5151\u5B8C",
        redeemFailPoints: "\u79EF\u5206\u4E0D\u8DB3\uFF0C\u6682\u65F6\u65E0\u6CD5\u5151\u6362",
        redeemFailMissing: "\u672A\u627E\u5230\u8BE5\u6743\u76CA",
        redeemFailGeneric: "\u5151\u6362\u5931\u8D25\uFF0C\u8BF7\u7A0D\u540E\u518D\u8BD5",
        networkError: "\u7F51\u7EDC\u5F02\u5E38",
        loadError: "\u52A0\u8F7D\u5931\u8D25"
      },
      categoryOptions: [{
        code: 'ALL',
        label: "\u5168\u90E8"
      }, {
        code: 'DINING',
        label: "\u9910\u996E"
      }, {
        code: 'STATIONERY',
        label: "\u5B66\u4E60"
      }, {
        code: 'CERT',
        label: "\u8363\u8A89"
      }]
    };
  },
  computed: {
    filteredBenefits: function filteredBenefits() {
      var _this = this;
      var source = this.activeCategory === 'ALL' ? this.benefits : this.benefits.filter(function (item) {
        return item.category === _this.activeCategory;
      });
      return (0, _toConsumableArray2.default)(source).sort(function (a, b) {
        if (a.available === b.available) {
          return (a.point || 0) - (b.point || 0);
        }
        return a.available ? -1 : 1;
      });
    },
    displayBenefits: function displayBenefits() {
      var _this2 = this;
      return this.filteredBenefits.map(function (item) {
        return _objectSpread(_objectSpread({}, item), {}, {
          imageSrc: _this2.resolveBenefitImage(item),
          iconText: item.icon || _this2.defaultBenefitIcon,
          stockLabel: item.available ? _this2.stockText(item.stock) : _this2.texts.soldOut,
          accentStyle: _this2.categoryAccentStyle(item.category),
          badgeStyle: _this2.categoryBadgeStyle(item.category),
          arrowStyle: _this2.categoryArrowStyle(item.category),
          canRedeemNow: _this2.canRedeem(item),
          actionText: _this2.redeemButtonText(item)
        });
      });
    },
    displayRecords: function displayRecords() {
      var _this3 = this;
      return this.records.map(function (record) {
        return _objectSpread(_objectSpread({}, record), {}, {
          cardClass: _this3.cardClass(record.category),
          iconText: record.icon || _this3.getCategoryIcon(record.category),
          statusClass: _this3.statusClass(record.status),
          timeText: _this3.formatTime(record.createTime)
        });
      });
    },
    activeBenefitCardClass: function activeBenefitCardClass() {
      return this.activeBenefit ? this.cardClass(this.activeBenefit.category) : 'card-default';
    },
    activeBenefitImage: function activeBenefitImage() {
      return this.activeBenefit ? this.resolveBenefitImage(this.activeBenefit) : '';
    },
    activeBenefitStockText: function activeBenefitStockText() {
      if (!this.activeBenefit) {
        return '';
      }
      return this.activeBenefit.available ? this.stockText(this.activeBenefit.stock) : this.texts.soldOut;
    },
    activeBenefitCanRedeem: function activeBenefitCanRedeem() {
      return this.activeBenefit ? this.canRedeem(this.activeBenefit) : false;
    },
    activeBenefitActionText: function activeBenefitActionText() {
      return this.activeBenefit ? this.redeemButtonText(this.activeBenefit) : this.texts.redeemNow;
    },
    sceneCards: function sceneCards() {
      return [{
        code: 'DINING',
        label: "\u9910\u996E",
        icon: this.getCategoryIcon('DINING'),
        count: this.getCategoryCount('DINING'),
        cardClass: 'scene-dining'
      }, {
        code: 'STATIONERY',
        label: "\u5B66\u4E60",
        icon: this.getCategoryIcon('STATIONERY'),
        count: this.getCategoryCount('STATIONERY'),
        cardClass: 'scene-library'
      }, {
        code: 'CERT',
        label: "\u8363\u8A89",
        icon: this.getCategoryIcon('CERT'),
        count: this.getCategoryCount('CERT'),
        cardClass: 'scene-cert'
      }];
    }
  },
  onLoad: function onLoad() {
    var username = uni.getStorageSync('username');
    if (!username) {
      uni.reLaunch({
        url: '/pages/login/login'
      });
      return;
    }
    this.username = username;
    this.initParticleStyles();
  },
  onShow: function onShow() {
    if (this.username) {
      this.loadAll();
    }
  },
  methods: {
    initParticleStyles: function initParticleStyles() {
      var styles = [];
      for (var i = 0; i < 36; i++) {
        styles.push({
          left: Math.random() * 100 + '%',
          animationDuration: 8 + Math.random() * 8 + 's',
          animationDelay: Math.random() * 6 + 's',
          width: 6 + Math.random() * 10 + 'rpx',
          height: 6 + Math.random() * 10 + 'rpx',
          opacity: 0.18 + Math.random() * 0.28
        });
      }
      this.particleStyles = styles;
    },
    loadAll: function loadAll() {
      var _this4 = this;
      return (0, _asyncToGenerator2.default)( /*#__PURE__*/_regenerator.default.mark(function _callee() {
        return _regenerator.default.wrap(function _callee$(_context) {
          while (1) {
            switch (_context.prev = _context.next) {
              case 0:
                _context.next = 2;
                return Promise.all([_this4.loadProfile(), _this4.loadBenefits(), _this4.loadRecords()]);
              case 2:
              case "end":
                return _context.stop();
            }
          }
        }, _callee);
      }))();
    },
    loadProfile: function loadProfile() {
      var _this5 = this;
      return (0, _asyncToGenerator2.default)( /*#__PURE__*/_regenerator.default.mark(function _callee2() {
        var res;
        return _regenerator.default.wrap(function _callee2$(_context2) {
          while (1) {
            switch (_context2.prev = _context2.next) {
              case 0:
                _context2.prev = 0;
                _context2.next = 3;
                return (0, _request.getProfile)(_this5.username);
              case 3:
                res = _context2.sent;
                _this5.userPoints = res.points || 0;
                _context2.next = 10;
                break;
              case 7:
                _context2.prev = 7;
                _context2.t0 = _context2["catch"](0);
                console.error('profile load failed', _context2.t0);
              case 10:
              case "end":
                return _context2.stop();
            }
          }
        }, _callee2, null, [[0, 7]]);
      }))();
    },
    loadBenefits: function loadBenefits() {
      var _this6 = this;
      return (0, _asyncToGenerator2.default)( /*#__PURE__*/_regenerator.default.mark(function _callee3() {
        var res;
        return _regenerator.default.wrap(function _callee3$(_context3) {
          while (1) {
            switch (_context3.prev = _context3.next) {
              case 0:
                _this6.loadingCatalog = true;
                _context3.prev = 1;
                _context3.next = 4;
                return (0, _request.getProductList)(1, 12);
              case 4:
                res = _context3.sent;
                _this6.benefits = res.list || [];
                _context3.next = 12;
                break;
              case 8:
                _context3.prev = 8;
                _context3.t0 = _context3["catch"](1);
                console.error('benefit load failed', _context3.t0);
                uni.showToast({
                  title: _this6.texts.loadError,
                  icon: 'none'
                });
              case 12:
                _context3.prev = 12;
                _this6.loadingCatalog = false;
                return _context3.finish(12);
              case 15:
              case "end":
                return _context3.stop();
            }
          }
        }, _callee3, null, [[1, 8, 12, 15]]);
      }))();
    },
    loadRecords: function loadRecords() {
      var _this7 = this;
      return (0, _asyncToGenerator2.default)( /*#__PURE__*/_regenerator.default.mark(function _callee4() {
        return _regenerator.default.wrap(function _callee4$(_context4) {
          while (1) {
            switch (_context4.prev = _context4.next) {
              case 0:
                _this7.loadingRecords = true;
                _context4.prev = 1;
                _context4.next = 4;
                return (0, _request.getExchangeRecords)(_this7.username);
              case 4:
                _this7.records = _context4.sent;
                _context4.next = 11;
                break;
              case 7:
                _context4.prev = 7;
                _context4.t0 = _context4["catch"](1);
                console.error('record load failed', _context4.t0);
                _this7.records = [];
              case 11:
                _context4.prev = 11;
                _this7.loadingRecords = false;
                return _context4.finish(11);
              case 14:
              case "end":
                return _context4.stop();
            }
          }
        }, _callee4, null, [[1, 7, 11, 14]]);
      }))();
    },
    getCategoryCount: function getCategoryCount(category) {
      return this.benefits.filter(function (item) {
        return item.category === category && item.available;
      }).length;
    },
    resolveBenefitImage: function resolveBenefitImage(item) {
      if (!item) {
        return '';
      }
      if (item.image) {
        return item.image;
      }
      var name = item.name || '';
      if (name.indexOf("\u5957\u9910\u5238") !== -1) {
        return '/static/mall/dining-meal-coupon.png';
      }
      if (name.indexOf("\u996E\u54C1") !== -1) {
        return '/static/mall/dining-drink-coupon.png';
      }
      if (name.indexOf("\u5706\u73E0\u7B14") !== -1) {
        return '/static/mall/stationery-pen.png';
      }
      if (name.indexOf("\u94C5\u7B14") !== -1) {
        return '/static/mall/stationery-pencil.png';
      }
      if (name.indexOf("\u7EFF\u8272\u5148\u950B") !== -1) {
        return '/static/mall/cert-green-pioneer.png';
      }
      if (name.indexOf("\u4F4E\u78B3\u4E4B\u661F") !== -1) {
        return '/static/mall/cert-low-carbon-star.png';
      }
      return '';
    },
    getCategoryIcon: function getCategoryIcon(category) {
      if (category === 'DINING') {
        return "\uD83C\uDF5D";
      }
      if (category === 'STATIONERY') {
        return "\uD83D\uDCDD";
      }
      if (category === 'CERT') {
        return "\uD83C\uDFC5";
      }
      return this.defaultBenefitIcon;
    },
    categoryAccentStyle: function categoryAccentStyle(category) {
      if (category === 'DINING') {
        return 'background: linear-gradient(90deg, #6bc88f, #4dad71);';
      }
      if (category === 'STATIONERY') {
        return 'background: linear-gradient(90deg, #73c5d4, #4fa8bb);';
      }
      if (category === 'CERT') {
        return 'background: linear-gradient(90deg, #ffbe63, #f39c3b);';
      }
      return 'background: linear-gradient(90deg, #b8d9c2, #9cc7aa);';
    },
    categoryBadgeStyle: function categoryBadgeStyle(category) {
      if (category === 'DINING') {
        return 'background: rgba(107, 200, 143, 0.14); color: #2c8551;';
      }
      if (category === 'STATIONERY') {
        return 'background: rgba(115, 197, 212, 0.16); color: #2d7f8d;';
      }
      if (category === 'CERT') {
        return 'background: rgba(255, 190, 99, 0.18); color: #b96e16;';
      }
      return 'background: rgba(156, 199, 170, 0.18); color: #4f7c5f;';
    },
    categoryArrowStyle: function categoryArrowStyle(category) {
      if (category === 'DINING') {
        return 'color: #43a464;';
      }
      if (category === 'STATIONERY') {
        return 'color: #3f95a7;';
      }
      if (category === 'CERT') {
        return 'color: #df8d2f;';
      }
      return 'color: #6b9b78;';
    },
    cardClass: function cardClass(category) {
      if (category === 'DINING') {
        return 'card-dining';
      }
      if (category === 'STATIONERY') {
        return 'card-library';
      }
      if (category === 'CERT') {
        return 'card-cert';
      }
      return 'card-default';
    },
    statusClass: function statusClass(status) {
      return status && status.indexOf("\u5DF2") === 0 ? 'status-ready' : 'status-pending';
    },
    stockText: function stockText(stock) {
      return "\u5E93\u5B58 " + (stock || 0);
    },
    selectCategory: function selectCategory(code) {
      this.activeCategory = code;
    },
    openBenefit: function openBenefit(item) {
      this.activeBenefit = item;
    },
    closeDetail: function closeDetail() {
      this.activeBenefit = null;
      this.redeeming = false;
    },
    canRedeem: function canRedeem(item) {
      return !!item && !!item.available && this.userPoints >= (item.point || 0);
    },
    redeemButtonText: function redeemButtonText(item) {
      if (!item.available) {
        return this.texts.soldOutAction;
      }
      if (this.userPoints < (item.point || 0)) {
        return this.texts.lackPoints;
      }
      return this.texts.redeemNow;
    },
    confirmRedeem: function confirmRedeem(item) {
      var _this8 = this;
      return new Promise(function (resolve) {
        uni.showModal({
          title: _this8.texts.confirmRedeemTitle,
          content: _this8.texts.confirmRedeemPrefix + item.point + _this8.texts.confirmRedeemMiddle + _this8.texts.pointUnit + _this8.texts.confirmRedeemSuffix + item.name,
          success: function success(res) {
            return resolve(!!res.confirm);
          },
          fail: function fail() {
            return resolve(false);
          }
        });
      });
    },
    resolveRedeemError: function resolveRedeemError(code) {
      if (code === 'RIGHT_SOLD_OUT') {
        return this.texts.redeemFailSoldOut;
      }
      if (code === 'POINTS_NOT_ENOUGH') {
        return this.texts.redeemFailPoints;
      }
      if (code === 'RIGHT_NOT_FOUND') {
        return this.texts.redeemFailMissing;
      }
      return this.texts.redeemFailGeneric;
    },
    performRedeem: function performRedeem(item, shouldCloseDetail) {
      var _this9 = this;
      return (0, _asyncToGenerator2.default)( /*#__PURE__*/_regenerator.default.mark(function _callee5() {
        var confirmed, res;
        return _regenerator.default.wrap(function _callee5$(_context5) {
          while (1) {
            switch (_context5.prev = _context5.next) {
              case 0:
                if (!(!item || _this9.redeeming)) {
                  _context5.next = 2;
                  break;
                }
                return _context5.abrupt("return");
              case 2:
                if (item.available) {
                  _context5.next = 5;
                  break;
                }
                uni.showToast({
                  title: _this9.texts.soldOutAction,
                  icon: 'none'
                });
                return _context5.abrupt("return");
              case 5:
                if (!(_this9.userPoints < (item.point || 0))) {
                  _context5.next = 8;
                  break;
                }
                uni.showToast({
                  title: _this9.texts.lackPoints,
                  icon: 'none'
                });
                return _context5.abrupt("return");
              case 8:
                _context5.next = 10;
                return _this9.confirmRedeem(item);
              case 10:
                confirmed = _context5.sent;
                if (confirmed) {
                  _context5.next = 13;
                  break;
                }
                return _context5.abrupt("return");
              case 13:
                _this9.activeBenefit = item;
                _this9.redeeming = true;
                _context5.prev = 15;
                _context5.next = 18;
                return (0, _request.exchangeProduct)(_this9.username, item.id);
              case 18:
                res = _context5.sent;
                if (!(res.code === 200)) {
                  _context5.next = 26;
                  break;
                }
                _context5.next = 22;
                return Promise.all([_this9.loadProfile(), _this9.loadBenefits(), _this9.loadRecords()]);
              case 22:
                _context5.next = 24;
                return _this9.showRedeemSuccess(res, item);
              case 24:
                if (shouldCloseDetail) {
                  _this9.closeDetail();
                }
                return _context5.abrupt("return");
              case 26:
                uni.showToast({
                  title: _this9.resolveRedeemError(res.message),
                  icon: 'none'
                });
                _context5.next = 33;
                break;
              case 29:
                _context5.prev = 29;
                _context5.t0 = _context5["catch"](15);
                console.error('redeem failed', _context5.t0);
                uni.showToast({
                  title: _this9.texts.networkError,
                  icon: 'none'
                });
              case 33:
                _context5.prev = 33;
                _this9.redeeming = false;
                return _context5.finish(33);
              case 36:
              case "end":
                return _context5.stop();
            }
          }
        }, _callee5, null, [[15, 29, 33, 36]]);
      }))();
    },
    showRedeemSuccess: function showRedeemSuccess(res, item) {
      var _this10 = this;
      var isGreenCertificate = res.productCode === 'CERT_GREEN_PIONEER';
      var isBadge = res.productCode === 'CERT_LOW_CARBON_STAR';
      var content = isBadge ? this.texts.badgeSuccessHint : res.claimMessage || item.exchangeHint;
      return new Promise(function (resolve) {
        uni.showModal({
          title: _this10.texts.redeemSuccessTitle,
          content: content,
          showCancel: isGreenCertificate,
          confirmText: isGreenCertificate ? _this10.texts.redeemSuccessView : _this10.texts.close,
          cancelText: _this10.texts.redeemSuccessDone,
          success: function success(modalRes) {
            if (isGreenCertificate && modalRes.confirm) {
              uni.navigateTo({
                url: '/pages/certificate/certificate?mode=benefit&productCode=CERT_GREEN_PIONEER'
              });
            }
            resolve();
          },
          fail: function fail() {
            return resolve();
          }
        });
      });
    },
    redeemActiveBenefit: function redeemActiveBenefit() {
      var _this11 = this;
      return (0, _asyncToGenerator2.default)( /*#__PURE__*/_regenerator.default.mark(function _callee6() {
        return _regenerator.default.wrap(function _callee6$(_context6) {
          while (1) {
            switch (_context6.prev = _context6.next) {
              case 0:
                _context6.next = 2;
                return _this11.performRedeem(_this11.activeBenefit, true);
              case 2:
              case "end":
                return _context6.stop();
            }
          }
        }, _callee6);
      }))();
    },
    handleCardRedeem: function handleCardRedeem(item) {
      var _this12 = this;
      return (0, _asyncToGenerator2.default)( /*#__PURE__*/_regenerator.default.mark(function _callee7() {
        return _regenerator.default.wrap(function _callee7$(_context7) {
          while (1) {
            switch (_context7.prev = _context7.next) {
              case 0:
                _context7.next = 2;
                return _this12.performRedeem(item, false);
              case 2:
              case "end":
                return _context7.stop();
            }
          }
        }, _callee7);
      }))();
    },
    formatTime: function formatTime(value) {
      if (!value) {
        return '';
      }
      return String(value).replace('T', ' ').slice(0, 16);
    }
  }
};
exports.default = _default;
/* WEBPACK VAR INJECTION */}.call(this, __webpack_require__(/*! ./node_modules/@dcloudio/uni-mp-weixin/dist/index.js */ 2)["default"]))

/***/ }),

/***/ 61:
/*!*************************************************************************************************************************!*\
  !*** C:/Users/xyj/Desktop/green/uni-app项目/pages/mall/mall.vue?vue&type=style&index=0&id=2e142186&scoped=true&lang=css& ***!
  \*************************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_6_oneOf_1_0_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_6_oneOf_1_1_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_6_oneOf_1_2_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_6_oneOf_1_3_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_mall_vue_vue_type_style_index_0_id_2e142186_scoped_true_lang_css___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! -!../../../../../../../Hbuilders/HBuilderX/plugins/uniapp-cli/node_modules/mini-css-extract-plugin/dist/loader.js??ref--6-oneOf-1-0!../../../../../../../Hbuilders/HBuilderX/plugins/uniapp-cli/node_modules/css-loader/dist/cjs.js??ref--6-oneOf-1-1!../../../../../../../Hbuilders/HBuilderX/plugins/uniapp-cli/node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib/loaders/stylePostLoader.js!../../../../../../../Hbuilders/HBuilderX/plugins/uniapp-cli/node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--6-oneOf-1-2!../../../../../../../Hbuilders/HBuilderX/plugins/uniapp-cli/node_modules/postcss-loader/src??ref--6-oneOf-1-3!../../../../../../../Hbuilders/HBuilderX/plugins/uniapp-cli/node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!../../../../../../../Hbuilders/HBuilderX/plugins/uniapp-cli/node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!./mall.vue?vue&type=style&index=0&id=2e142186&scoped=true&lang=css& */ 62);
/* harmony import */ var _Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_6_oneOf_1_0_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_6_oneOf_1_1_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_6_oneOf_1_2_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_6_oneOf_1_3_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_mall_vue_vue_type_style_index_0_id_2e142186_scoped_true_lang_css___WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_6_oneOf_1_0_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_6_oneOf_1_1_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_6_oneOf_1_2_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_6_oneOf_1_3_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_mall_vue_vue_type_style_index_0_id_2e142186_scoped_true_lang_css___WEBPACK_IMPORTED_MODULE_0__);
/* harmony reexport (unknown) */ for(var __WEBPACK_IMPORT_KEY__ in _Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_6_oneOf_1_0_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_6_oneOf_1_1_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_6_oneOf_1_2_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_6_oneOf_1_3_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_mall_vue_vue_type_style_index_0_id_2e142186_scoped_true_lang_css___WEBPACK_IMPORTED_MODULE_0__) if(["default"].indexOf(__WEBPACK_IMPORT_KEY__) < 0) (function(key) { __webpack_require__.d(__webpack_exports__, key, function() { return _Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_6_oneOf_1_0_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_6_oneOf_1_1_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_6_oneOf_1_2_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_6_oneOf_1_3_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_mall_vue_vue_type_style_index_0_id_2e142186_scoped_true_lang_css___WEBPACK_IMPORTED_MODULE_0__[key]; }) }(__WEBPACK_IMPORT_KEY__));
 /* harmony default export */ __webpack_exports__["default"] = (_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_6_oneOf_1_0_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_6_oneOf_1_1_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_6_oneOf_1_2_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_6_oneOf_1_3_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_Hbuilders_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_mall_vue_vue_type_style_index_0_id_2e142186_scoped_true_lang_css___WEBPACK_IMPORTED_MODULE_0___default.a); 

/***/ }),

/***/ 62:
/*!*****************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************!*\
  !*** ./node_modules/mini-css-extract-plugin/dist/loader.js??ref--6-oneOf-1-0!./node_modules/css-loader/dist/cjs.js??ref--6-oneOf-1-1!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib/loaders/stylePostLoader.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--6-oneOf-1-2!./node_modules/postcss-loader/src??ref--6-oneOf-1-3!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!C:/Users/xyj/Desktop/green/uni-app项目/pages/mall/mall.vue?vue&type=style&index=0&id=2e142186&scoped=true&lang=css& ***!
  \*****************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

// extracted by mini-css-extract-plugin
    if(false) { var cssReload; }
  

/***/ })

},[[55,"common/runtime","common/vendor"]]]);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/mall/mall.js.map