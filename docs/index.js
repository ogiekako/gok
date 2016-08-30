// Copied from http://qiita.com/DUxCA/items/27b7b865a0ab28b5d530.
window.addEventListener("DOMContentLoaded", main);
window.addEventListener("hashchange", main);

var get = $.get;
var renderer = new marked.Renderer();
renderer.code = function(code, language) {
  var highlighted;
  if (!language) {
    highlighted = hljs.highlightAuto(code).value;
  } else {
    var validLang = !!(language && hljs.getLanguage(language));
    if (!validLang) {
      console.warn(language + ' is not a valid language.');
    }
    highlighted = validLang ? hljs.highlight(language, code).value : code;
  }
  return '<pre><code class="hljs ' + language + '">' + highlighted + '</code></pre>';
};
marked.setOptions({
  renderer: renderer,
  gfm: true,
  tables: true,
  // 改行を無視しない
  breaks: true,
  smartLists: false,
  langPrefix: '',
});

function main(){
  // ルーティング
  var name = "";
  if( location.hash.length <= 1 ){
    name = "index.md";
    location.hash = "#" + name;
  }else{
    name = location.hash.slice(1);
  }
  // ページ内リンクなのでhistory.pushStateする必要はない
  get(name).catch(function(){
    return Promise.resolve("# 404 page not found");
  }).then(function(text){
    var html = marked(text);
    // mathjaxで処理
    var div = document.getElementById("content");
    div.innerHTML = html;
  }).catch(function(err){
    // jqueryのpromiseはthenの中でエラー吐いて止まってもconsoleに表示してくれないので表示させる
    console.error(err);
  });
}
