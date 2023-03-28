<!-- Matomo -->
<script data-name="matomo"  data-category="analytics" type="text/plain">
var _paq = _paq || [];
  /* tracker methods like "setCustomDimension" should be called before "trackPageView" */
  console.log(document.URL);

  var regexOverview = /https:\/\/pure\.mpg\.de\/pubman\/faces\/ViewItemOverviewPage\.jsp\?itemId=(item_\d+)_?\d?.*/;
  var matchOverview = regexOverview.exec(document.URL);
  var regexFull = /https:\/\/pure\.mpg\.de\/pubman\/faces\/ViewItemFullPage\.jsp\?itemId=(item_\d+)_?\d?.*/;
  var matchFull = regexFull.exec(document.URL);

  if (matchOverview != null && matchOverview[1] != null && matchOverview[1] !== undefined) {
    _paq.push(['setCustomUrl', 'https://pure.mpg.de/pubman/item/' + matchOverview[1]]);
  }
  else if (matchFull != null && matchFull[1] != null && matchFull[1] !== undefined) {
    _paq.push(['setCustomUrl', 'https://pure.mpg.de/pubman/item/' + matchFull[1]]);
  }
  
  _paq.push(['trackPageView']);
  _paq.push(["disableCookies"]);

  (function() {
    var u="//analytics.mpdl.mpg.de/";
    
    if (document.URL.toString() === 'https://pure.mpg.de/pubman/faces/ViewItemFullPage.jsp' ||
      document.URL.toString() === 'https://pure.mpg.de/pubman/faces/ViewItemOverviewPage.jsp') {
        return;
    }


    _paq.push(['setTrackerUrl', u+'piwik.php']);
    _paq.push(['setSiteId', '1']);

    var d=document, g=d.createElement('script'), s=d.getElementsByTagName('script')[0];
    g.type='text/javascript'; g.async=true; g.defer=true; g.src=u+'piwik.js'; s.parentNode.insertBefore(g,s);
  })();
</script>
<script defer src="https://assets.mpdl.mpg.de/static/mpdl-consent/consent-config-matomo.js"></script>
<script defer>
    window.addEventListener('load', ()=> {
        runConsentBanner({'privacyPolicyUrl' : 'https://colab.mpdl.mpg.de/mediawiki/MPG.PuRe_Datenschutzhinweis'});
    });
</script>
