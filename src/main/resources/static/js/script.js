console.log("this is script file");

const toggleSidebar = () => {
    if ($(".sidebar").is(":visible")) {
        //true-->close the sidebar
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%")
    } else {
        //false-->show the sidebar
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%")
    }
};

search = () => {
    // console.log("searching in progress...");
    let query = $("#search-input").val();
    if (query === "")
        $(".search-result").hide();
    else {
        // console.log(query);
        //sending the request to server
        let url = `http://localhost:8080/search/${query}`;
        fetch(url)
            .then((response) => {
                return response.json();
            })
            .then((data) => {
                // console.log(data);
                let text = `<div class='list-group>`;
                data.forEach((contact) => {
                    text += `<a href='/user/${contact.contactId}/contacts'   class='list-group-item list-group-item-action'>${contact.name}</a>`;
                });
                text += `</div>`;
                $(".search-result").html(text);
                $(".search-result").show();
            });
    }
};