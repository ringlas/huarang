var menu, textContainer, id = 0;

Array.prototype.last = function() {
    return this[ this.length-1 ];
}

$(document).ready(function () {
    textContainer = document.getElementById('textContainer');
    initStaticMenu();
    initFloatingMenu();

    $(window).resize( function() {
        resizeTextArea();
    });

    $(document).mousedown( function( e ) {
        if( e.button == 2 && targetIsInside( e.target, textContainer ) ) {
            menu.show( e.target, e.pageX, e.pageY );
            return false;
        } else if( menu.visible && !targetIsInside( e.target, menu[0] ) ) {
            menu.hide();
        }
    });

    document.oncontextmenu = function() {return false;};

    resizeTextArea();
});

function triggerInputField( id ) {
    $('#fileForm').find('#'+id).val('');
    $('#fileInputLabel').html('Няма избран файл.');
    $('#'+id).trigger('click');
}

function fileSelected() {
    var file = document.getElementById('fileField').files[0];

    if (file) {
        $('#fileInputLabel').html(file.name);
        var reader = new FileReader();
        reader.onload = function (e) {
            var text = reader.result;
            textContainer.innerHTML = processText(text);
            textContainer.scrollTop = 0;
            resizeTextArea();
        }

        reader.readAsText(file, '');
    }
    else {
        $('#fileInputLabel').html('Няма избран файл.');
    }
}

function getBook() {
    var title, content;
    var text = document.getElementById( 'textContainer').innerHTML;
    //title = text.split( ''<br>' ')[0];
    title = "Ledenite pirati";
    content = text.replace('<br>', ',,,');
    return {
        title: title,
        episodes: content
    };
}

function resizeTextArea() {
    $('.textContainer').css({
        height: window.innerHeight - 285
    });
}

function filterDownArray( map, len ) {
    //calculate min offsets for each number
    var minOffsets = [],
        newCandidates = [],
        count = 0,
        i,
        min,
        m;

    minOffsets[0] = map[0] ? map[0][0] : 0;
    for (i = 1; i < len; i++) {
        minOffsets[i] = Math.max( minOffsets[ i - 1 ], ( map[i] ? map[i][0] : 0 ) );
    }

    newCandidates[0] = map[0];
    //rule out all of the above, based on minOffsets (chapter 3 will surely come before chapter 4)
    for (i = 1; i < len; i++) {
        m = map[i];
        if( m ) {
            min = minOffsets[i - 1];
            var oldLen = m.length;
            newCandidates[i] = $.grep( m, function( offset ) {
                return ( offset > min );
            } );

            count += ( oldLen - newCandidates[i].length );
        }
    }

    return {
        count: count,
        records: newCandidates
    };
}

function filterUpArray( map, len ) {
    //calculate max offsets
    var maxOffsets = [],
        newCandidates = [],
        count = 0,
        i,
        m,
        max,
        maxOffset = 1 << 30;

    maxOffsets[len - 1] = ( map[len - 1] ? map[len - 1].last() : maxOffset );
    for (i = len - 2; i >= 0; i--) {
        maxOffsets[i] = Math.min( maxOffsets[ i + 1 ], ( map[i] ? map[i].last() : maxOffset ) );
    }

    for (i = len - 2; i >= 0; i--) {
        m = map[i]
        if( m ) {
            max = maxOffsets[i + 1];
            var oldLen = m.length;
            newCandidates[i] = $.grep( m, function( offset ) {
                return ( offset < max );
            } );

            count += ( oldLen - newCandidates[i].length );
        }
    }

    return {
        count: count,
        records: newCandidates
    };
}

function trimMapLength( map, len ) {
    var miss = 5;
    var realLen = len;
    for ( var i = 0; i < len; i++) {
        if( typeof map[i] == 'undefined' ) {
            miss--;

            if(! miss ) {
                break;
            }
        } else {
            realLen = i
        }
    }

    return realLen
}

function processText(text) {
    var max = -1;

    var id = 1,
        numbersMap = {};

    //map all numbers encountered in the text
    text = text.replace(/(\d+)/g, function (match, number, offset, string) {

        max = Math.max(max, number) + 1;

        if (!numbersMap[number]) {
            numbersMap[number] = [];
        }
        numbersMap[number].push(offset);

        return number;
    });

    //re-calculate the max chapter if there are occurences of big numbers in the book (like year 2013, etc..)
    var max = trimMapLength( numbersMap, max );
    console.log( 'Chapters cut down to: ' + max );

    //we don't believe that there would be chapter "0", so we neglect it
    delete numbersMap[0];

    var arr = numbersMap,
        res,
        i,
        count = -1;

    //keep goind until no further filtering is possible
    while( count != 0 ) {
        res = filterDownArray( arr, max );
        arr = res.records;
        count = res.count;

        res = filterUpArray( arr, max );
        arr = res.records;
        count += res.count;

        console.log( count + ' filtered' );
    }

    //get the chapter candidates that remain unresolved
        count = 0;
        var unresolved = [];
    for (i = arr.length - 1; i >= 0; i--) {
        if( arr[i] && arr[i].length > 1 ) {
            count++;
            unresolved[i] = arr[i];
        }

        else {
            unresolved[i] = false;
        }
    }

    console.log( count + ' still unresolved. Starting heuristics.' );

    //filter the unresolved based on appearance of newlines around them
    //if not any, pick one closer to the beginning / end depending on the chapter number
    //lower chapter number is more likely to appear later in the subset.
    var j, unr;
    count = 0;
    var count2 = 0;
    for( i = unresolved.length - 1; i>=0; i--) {
        unr = unresolved[i];
        if( !unr ) {
            continue;
        }

        for( j = unr.length - 1; j >= 0; j--) {
            var reg = new RegExp( i + '\\s*[\\n|\\r]|[\\n|\\r]\\s*' + i );
            if( reg.test( text.substr( unr[j] - 10, 20 ) ) ) {
                arr[i] = [unr[j]];
                count++;
                break;
            }
        }

        //no match found
        //pick by precedence (almost random)
        if( j == -1 ) {
            arr[i] = i > max / 2 ? [arr[i][0]] : [arr[i].last()];
        }
    }

    console.log( count + ' matched by new line');
    console.log( count2 + ' selected by precedence (weighed random)');

    //mark the numbers we have decided are the chapter titles
    //so we can distinguish between them and the navigation links
    for (i = arr.length - 1; i >= 0; i--) {
        if (arr[i] && arr[i].length == 1) {
            var off =  arr[i][0];

            text = text.substr(0, off) + '#*#' + i + '#*#' + text.substr(off + i.toString().length );
        }
    }

    //create navigation links
    text = text.replace(/[^#\*#\d](\d+)[^\d#\*#]/g, function (match, number, offset, string) {
        return match.replace( number, getLinkString( number ) );
    });

    //create chapter separators
    text = text.replace( /#\*#(\d+)#\*#/g, function (match, number, offset, string) {
        return getChapterString( number );
    });

    //new lines to <br>
    return text.replace( /[\r\n|\n\r|\n|\r]/g, '<br>');
    return text;
}

function targetIsInside( target, element ) {
    if( target == element ) {
        return true;
    }

    if( target.parentNode ) {
        return targetIsInside( target.parentNode, element );
    }

    return false;
}

function toggleButtonState( btn, pressed ){
    if( pressed == true ) {
        btn.classList.add( 'btn-success' );
        btn.classList.remove( 'btn-danger' );
    } else if( pressed == false ){
        btn.classList.remove( 'btn-success' );
        btn.classList.add( 'btn-danger' );
    } else {
        btn.classList.toggle( 'btn-success' );
        btn.classList.toggle( 'btn-danger' );
    }
}

function getSelectedHtml() {
    var selection = window.getSelection();
    if ( selection.rangeCount > 0 ) {
        var range = selection.getRangeAt(0),
            clonedSelection = range.cloneContents(),
            div = document.createElement('div');

        div.appendChild(clonedSelection);
        return div.innerHTML;
    }

    return false;
}

function trySetSelectedButtonsState( toggled ) {
    var selection = window.getSelection();
    if ( selection.rangeCount > 0 ) {
        var range = selection.getRangeAt(0),
            clonedSelection = range.cloneContents(),
            div = document.createElement('div');

        div.appendChild(clonedSelection);
        html = div.innerHTML;

        var btns = html.match( /ep_btn_(\d+)/g );
        if ( btns ) {
            $( btns ).each( function ( ind, str ) {
                toggleButtonState( document.getElementById( str ), toggled );
            } );
        } else if( toggled ) {
            //no buttons selected, grab the selection and convert it to button if possible
            textToLink( range );
        }
    }
}

function linkToChapter( link ) {
    textContainer.innerHTML = textContainer.innerHTML.replace( link.outerHTML, getChapterString( link.innerHTML ) );
}

function chapterToLink( chapter ) {
    textContainer.innerHTML = textContainer.innerHTML.replace( chapter.outerHTML, getLinkString( chapter.innerHTML ) );
}

function elementToText( element ) {
    textContainer.innerHTML = textContainer.innerHTML.replace( element.outerHTML, element.innerHTML );
}

function selectionToText() {
    var html = getSelectedHtml();
    if( html ) {
        var elements = html.match( /ep_[btn_]*(\d+)/g );
        if ( elements ) {
            $( elements ).each( function ( ind, str ) {
                elementToText( document.getElementById( str ) );
            } );
        }
    }
}

function convertSelectedToChapter( range ) {
    var selection = window.getSelection();
    if ( selection.rangeCount > 0 ) {
        var range = selection.getRangeAt(0);

        if( range.toString().length > 0 && range.startContainer == range.endContainer ) {
            var div = document.createElement('div');
            div.classList.add('chapter');
            div.id = 'ep_' + (id++);
            div.innerHTML = range.toString();

            range.deleteContents();
            range.insertNode(div);
        }
    }
}

function textToLink( range ) {
    if( range.toString().length > 0 && range.startContainer == range.endContainer ) {
        var div = document.createElement('div');
        div.classList.add('number');
        div.classList.add('btn');
        div.classList.add('btn-danger');
        div.addEventListener( 'click', function( e ) { toggleButtonState(e.target); } );
        div.id = 'ep_btn_' + (id++);
        div.innerHTML = range.toString();

        range.deleteContents();
        range.insertNode(div);
    }
}


//link:       <div class="number btn" id="ep_btn_15" onclick="toggleButtonState( this )">1</div>
//chapter:    <div class="chapter" id="ep_' + number + '">' + number + '</div>

function getLinkString( text ) {
    return '<div class="number btn-danger btn" id="ep_btn_' + (id++) + '" onclick="toggleButtonState( this )">' + text + '</div>';
}

function getChapterString( text ) {
    return '<div class="chapter" id="ep_' + (id++) + '">' + text + '</div>';
}

function initStaticMenu() {

    var staticMenu = $($( '#sideMenu' ));
    staticMenu.linkBtn = $(staticMenu.find('.icon-link')[0]);
    staticMenu.unlinkBtn = $(staticMenu.find('.icon-unlink')[0]);
    staticMenu.chapterBtn = $(staticMenu.find('.icon-ellipsis')[0]);
    staticMenu.deleteBtn = $(staticMenu.find('.icon-cancel-circled')[0]);
    staticMenu.clickHandler = function( btn, e ) {
        btn.action();

        e.stopPropagation();
        e.preventDefault();
        return false;
    };

    staticMenu.linkBtn.mousedown(function(e){
        e.preventDefault();
        e.stopPropagation();
    }).click( function( e ) { staticMenu.clickHandler( staticMenu.linkBtn, e ) } );

    staticMenu.unlinkBtn.mousedown(function(e){
        e.preventDefault();
        e.stopPropagation();
    }).click( function( e ) { staticMenu.clickHandler( staticMenu.unlinkBtn, e ) } );

    staticMenu.chapterBtn.mousedown(function(e){
        e.preventDefault();
        e.stopPropagation();
    }).click( function( e ) { staticMenu.clickHandler( staticMenu.chapterBtn, e ) } );

    staticMenu.deleteBtn.mousedown(function(e){
        e.preventDefault();
        e.stopPropagation();
    }).click( function( e ) { staticMenu.clickHandler( staticMenu.deleteBtn, e ) } );

    staticMenu.linkBtn.action = function() { trySetSelectedButtonsState( true ); };
    staticMenu.unlinkBtn.action = function() { trySetSelectedButtonsState( false ); };
    staticMenu.chapterBtn.action = function() { convertSelectedToChapter( getSelectedHtml() ) };
    staticMenu.deleteBtn.action = function() { selectionToText() };
}

function initFloatingMenu() {
    menu = $($('#floatingMenu'));
    menu.visible = false;
    menu.linkBtn = $(menu.find('.icon-link')[0]);
    menu.unlinkBtn = $(menu.find('.icon-unlink')[0]);
    menu.chapterBtn = $(menu.find('.icon-ellipsis')[0]);
    menu.deleteBtn = $(menu.find('.icon-cancel-circled')[0]);
    menu.hideLinkBtn = function() { menu.linkBtn.css({display: 'none'});};
    menu.hideUnlinkBtn = function() { menu.unlinkBtn.css({display: 'none'});};
    menu.hideChapterBtn = function() { menu.chapterBtn.css({display: 'none'});};
    menu.hideDeleteBtn = function() { menu.deleteBtn.css({display: 'none'});};
    menu.showLinkBtn = function() { menu.linkBtn.css({display: 'inline-block'});};
    menu.showUnlinkBtn = function() { menu.unlinkBtn.css({display: 'inline-block'});};
    menu.showChapterBtn = function() { menu.chapterBtn.css({display: 'inline-block'});};
    menu.showDeleteBtn = function() { menu.deleteBtn.css({display: 'inline-block'});};
    menu.hide = function() {
        menu.css({ left: -10000, top: -10000 });
        menu.visible = false;
    };
    menu.show = function( target, x, y ) {
        menu.css({ left: x, top: y });
        menu.visible = true;
        if( target.classList.contains('number') ) {
            menu.deleteBtn.action = function() { elementToText( target ) };

            if( target.classList.contains('btn-success') ) {
                menu.hideLinkBtn();
                menu.showUnlinkBtn();
                menu.showChapterBtn();

                menu.unlinkBtn.action = function() { toggleButtonState( target, false ) };
                menu.chapterBtn.action = function() { linkToChapter( target ) };
            } else {
                menu.showLinkBtn();
                menu.hideUnlinkBtn();
                menu.showChapterBtn();

                menu.linkBtn.action = function() { toggleButtonState(  target, true ) };
                menu.chapterBtn.action = function() { linkToChapter( target ) };
            }
        } else if( target.classList.contains('chapter')) {
            menu.showLinkBtn();
            menu.hideUnlinkBtn();
            menu.hideChapterBtn();

            menu.linkBtn.action = function() { chapterToLink( target ) };
            menu.deleteBtn.action = function() { elementToText( target ) };
        } else {
            menu.linkBtn.action = function() { trySetSelectedButtonsState( true ); };
            menu.unlinkBtn.action = function() { trySetSelectedButtonsState( false ); };
            menu.chapterBtn.action = function() { convertSelectedToChapter( getSelectedHtml() ) };
            menu.deleteBtn.action = function() { selectionToText() };

            menu.showLinkBtn();
            menu.showChapterBtn();
            menu.showUnlinkBtn();
        }

        var book = {
            chapters: [
                {
                    text: "",
                    links: []
                }
            ]
        }

    };

    menu.clickHandler = function( btn, e ) {
        btn.action();
        menu.hide();

        e.stopPropagation();
        e.preventDefault();
        return false;
    };

    menu.linkBtn.mousedown(function(e){
        e.preventDefault();
        e.stopPropagation();
    }).click( function( e ) { menu.clickHandler( menu.linkBtn, e ) } );

    menu.unlinkBtn.mousedown(function(e){
        e.preventDefault();
        e.stopPropagation();
    }).click( function( e ) { menu.clickHandler( menu.unlinkBtn, e ) } );

    menu.chapterBtn.mousedown(function(e){
        e.preventDefault();
        e.stopPropagation();
    }).click( function( e ) { menu.clickHandler( menu.chapterBtn, e ) } );

    menu.deleteBtn.mousedown(function(e){
        e.preventDefault();
        e.stopPropagation();
    }).click( function( e ) { menu.clickHandler( menu.deleteBtn, e ) } );
}