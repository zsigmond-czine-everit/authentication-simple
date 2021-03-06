authentication-simple
=====================

A simple principal and credential based authentication with management service.

#Database structure
##Simple subject table
 - **simple_subject_id**: The primary key of the simple subject.
 - **principal**: The unique identifier of the simple subject. It can be a 
 user name, email address or any other identifier the uniquely identifies the 
 simple subject.
 - **encrypted_credential**: The encrypted credential belonging to the 
 principal. It is used to verify the identity of the principal in an 
 authentication process. It can be a password, a pin code or any other 
 credential that is known only by the principal. For credential encryption 
 and matching the [credential-encryptor-api][2] is used.
 - **resource_id**: Foreign key to the resource table (provided by 
 [resource-ri][5] module). This is the Resource ID of the simple subject to 
 be able to use it for [authentication][6], [authorization][7], etc. The same 
 resource_id can be assigned to multiple simple subjects (it is not unique). 
 In that case multiple simple subjects will identify the same resource, so 
 they will share for e.g. their permissions. This depends on the business 
 application that uses the authentication simple module.

The simple subject table can be a good base of user management because it 
integrates the possibility of [authentication][6] and [authorization][7].
 
#Concept
Full authentication concept is available on blog post [Everit Authentication][1].
Implemented components based on this concept are listed [here][8].

[![Analytics](https://ga-beacon.appspot.com/UA-15041869-4/everit-org/authentication-simple)](https://github.com/igrigorik/ga-beacon)

[1]: http://everitorg.wordpress.com/2014/07/31/everit-authentication/
[2]: https://github.com/everit-org/credential-encryptor-api
[5]: https://github.com/everit-org/resource-ri
[6]: https://github.com/everit-org/authentication-context-api
[7]: https://github.com/everit-org/authorization-api
[8]: http://everitorg.wordpress.com/2014/07/31/everit-authentication-implemented-and-released-2/
