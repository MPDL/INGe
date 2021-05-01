/**
 * @author Marco Schlender
 * @created 2011-08-19
 */
/*
 * the function search any selectbox on the page and resize them if it's no selectbox in meta menu
 * it activates if the document is ready (after loaded)
 * @required jQuery 1.3.1 or greater
 * @parameter maxWidth = maximum width for the selectboxes
 * @notice if the content is greater than the maximum width it would be cutted
 */
function resizeSelectbox(maxWidth) {
    $('.selectContainer').each(function(i, box) {
        //check if the selectbox is on metaMenu
        if ($(box).parent().hasClass("metaMenu")) {
            //			console.log('Parent: ' + $(box).parent().attr('class'))
        } else {
            var element = $(box);
            //Define the current width of customized selectBox
            var curSelectWidth = element.find('select').width();

            //if the width of selectbox-width is smaller than the maxWidth  AND the selectContainer-width is greater than the selectbox-width -> it's allowed to resize
            if (curSelectWidth < maxWidth && element.width() > curSelectWidth) {
                //set the new width to the selectbox container
                element.find('div:first-child').width(curSelectWidth);
                //set the new width of selectionBox (info area) on the new width
                element.find('.selectionBox').width(curSelectWidth);
                //finally set the new width to the global element
                element.width(curSelectWidth);
            }
        }
    });
}

/*
 * the function update the text into selectionBox for viewing the selected option in selectbox
 * @parameter box = selectbox object
 * --- only for testing at the moment, maybe it would be neccessary in detail submission
 */
function updateSelectionBox(box, isStart) {
    /* the start param is different in workflow for selectionBox values 
     * if it's true: all selectboxes will be read and update their selectionBox
     * if it's false: only the current selectbox will be read and update their selectionBox
     */
    var tooltip = null;
    var selText = null;
    var selCont = null;
    var selVal = null;

    if (isStart) { //if start or ajax request every selectbox will be focused to update the selectionBox
        var sb = $('select');
        sb.each(function(i, slb) {
            selCont = null;
            selText = null;
            tooltip = null;
            selVal = null;

            selCont = $(slb).parents(".selectContainer");
            if (selCont && selCont.length > 0) {
                selVal = slb.options[slb.selectedIndex].text;
                $(slb).find("option").each(function(i, opt) {
                    if ($(opt).val() == selVal) {
                        selVal = $(opt).text();
                    }
                });

                selText = selCont.find(".selectionBox");
                if (selText && selText.length > 0) {
                    selText.html(selVal);
                }

                tooltip = selCont.find(".tooltip");
                if (tooltip && tooltip.length > 0) {
                    tooltip.html(selVal);
                    tooltip.css("min-width", selText.width() - 10);
                }
            }
        });
    } else {
        var contentText = box.options[box.selectedIndex].text;
        selCont = $(box).parent();
        selCont.find('.selectionBox').text(contentText);
        tooltip = selCont.find(".tooltip");
        if (tooltip && tooltip.length > 0) {
            tooltip.text(contentText);
            tooltip.css("min-width", selCont.find(".selectionBox").width() - 10);
        }
        tooltip = null;

        /* at first: exclude all logical operation selectboxes */
        if (contentText.trim() != 'AND' && contentText.trim() != 'OR' && contentText.trim() != 'NOT') {
            var parent = null; //define the parent object
            for (var ij = 0; ij < $(box).parents().length; ij++) {
                if ($($(box).parents().get(ij)).hasClass('.itemBlock')) {
                    parent = $($(box).parents().get(ij));
                    break;
                }
            }

            //compare the contentText and decide for remove hidden class
            if (parent) {
                if (contentText.trim() != '-' && contentText.trim() != '--' && contentText.trim() != '') {
                    parent.find('.itemBlockContent').removeClass("hideBlockIfVoid");
                    parent.find('.itemBlockContent').removeClass("hideAdvSearchComplexBlockIfVoid");
                    /* parent.find('.itemBlockContent').removeClass("hideAdvSearchGenreBlockIfVoid"); -- should be expanded for text-input fields*/
                }
            }
        }
    }
}

$(document).ready(function(e) {
    resizeSelectbox(431);
    updateSelectionBox(null, true);
});