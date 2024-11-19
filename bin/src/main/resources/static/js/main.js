function generateUsers(users, username, authorities) {
  let userList = document.getElementById("user-list");
  let html = "";
    if (users != null && users.length > 0) {
      users.forEach(user => {
          html += generateUser(user, username, authorities);
      });

  }
  userList.innerHTML = html;
}

function generateUser(user, username, authorities) {
  let html = '<div class="users">';
  html += '	<p class="username">' + user.username + "</p>";
  if (!(user.username === username)) {
    html += '		<a href="/view-profile?id=' + user.id + '" class="action action-view">View</a>';
    if (hasRole(authorities, "ROLE_ADMIN")) {
      html += '		<a href="/change-username?id=' + user.id + '" class="action action-mod">Change</a>';
      html += '		<div class="action action-del" onclick="handleDelUser(' + user.id + ", '" + user.username + "')\">Delete</div>";
    }
  }
  html += "</div>";
  return html;
}

function hasRole(authorities, role) {
  let result = false;
  authorities.forEach((authority) => {
    if (authority.authority === role) {
      result = true;
    }
  });
  return result;
}

function handleDelUser(id, username) {
  if (confirm(`Are you sure to remove user ${username}?`))
    location.replace("/del-user?id=" + id);
}


