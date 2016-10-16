require 'webrick'
require 'json'



BIN_DIR_PATH = File.expand_path("..", Dir.pwd) + '/bin/'
BINARY_NAME = 'am.exe'


class VersionController
  def self.latest_version(params=nil)
    appList = Dir.entries(BIN_DIR_PATH).sort
    appList.reverse!
     
    {latest_version: appList.first}
  end
  
  def self.latest_download(params=nil)
    f= File.new(BIN_DIR_PATH + latest_version[:latest_version].to_s + '/' +  BINARY_NAME)
    
  end
end


class AMUpdater < WEBrick::HTTPServlet::AbstractServlet
  def do_GET (request, response)
    params =  request.query
    path = request.path.gsub('/', '')
    
    response.status = 200
    response.content_type = 'application/json'
    result = nil
    
    case request.path
      when "/latest_version"
        result = VersionController.send(path, params)
        response.body = result.to_json
      when "/latest_download"
        result = VersionController.send(path, params)
        response.body = result
        response.content_type = 'application/octet-stream'
        response.header["Content-Length"] = File.size(result)
        response.header["Content-disposition"] = "attachment;filename=#{File.basename(File.expand_path(result))}"
      else
        result = "No routes"
    end
  end
end







server = WEBrick::HTTPServer.new(:Port => 9999)
server.mount "/", AMUpdater
trap("INT") {
    server.shutdown
}
server.start