/*
* OpenRemote, the Home of the Digital Home.
* Copyright 2008, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License as
* published by the Free Software Foundation; either version 3.0 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
*
* You should have received a copy of the GNU General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/

//--------------------------------------------------------------------------------------------------
//
// Vocabulary for APR types.
//
// APR works pretty hard to make their structures read only which sort of plays into the whole
// OO idea. So just to go an extra step and define the vocabulary similar to Java's. These are
// pointers to internal structures I have no visibility to, so let's treat them as such.
//
// And I don't care if you don't like it.
//
//
// Author: Juha Lindfors (juha@juhalindfors.com)
//
//--------------------------------------------------------------------------------------------------

#ifndef ORC_DAEMON_APRVOCABULARY_H
#define ORC_DAEMON_APRVOCABULARY_H

#include "apr_network_io.h"
#include "apr_thread_proc.h"



// Vocabulary -------------------------------------------------------------------------------------

typedef apr_socket_t *     Socket;
typedef Socket *           SocketResult;
typedef apr_sockaddr_t *   SocketAddress;
typedef apr_pool_t *       MemPool;
typedef MemPool *          MemPoolResult;
typedef apr_thread_t *     Thread;
typedef apr_threadattr_t * ThreadAttributes;
typedef apr_status_t       Status;

#endif
