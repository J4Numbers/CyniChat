function getPlayer() {
	$.post( 'scripts/getPlayer.php', { player : $('select.player').val() } ).done( function( data ) {
		
		$('span.out').html( data );
	});
}