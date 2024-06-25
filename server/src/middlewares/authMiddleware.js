const authMiddeware = {
  checkIfRightUser: (req, res, next) => {
    const validation = "do something";
    if (validation) {
      next();
    } else {
      res.status(403).json("No permission");
    }
  },
};

export default authMiddeware;
